package ch.uzh.ifi.access.student.evaluation;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine.Events;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachine.States;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachineFactory;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachineRepoService;
import ch.uzh.ifi.access.student.evaluation.process.ProcessStepFactoryService;
import ch.uzh.ifi.access.student.evaluation.process.step.ProcessStep;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EvalProcessService {

    private static final Logger logger = LoggerFactory.getLogger(EvalProcessService.class);

    private ProcessStepFactoryService stepFactory;

    private EvalMachineRepoService machineRepo;

    @Autowired
    public EvalProcessService(ProcessStepFactoryService stepFactory, EvalMachineRepoService machineRepo) {
        this.stepFactory = stepFactory;
        this.machineRepo = machineRepo;
    }

    public String initEvalProcess(StudentSubmission submission) {
        String processId = UUID.randomUUID().toString();
        try {
            StateMachine<States, Events> m = EvalMachineFactory.initSMForSubmission(submission.getId());
            m.startReactively().subscribe();
            machineRepo.store(processId, m);
        } catch (Exception e) {
            logger.error("Could not create state machine for submission: " + submission.getId());
            logger.error(e.getMessage());
        }

        return processId;
    }

    public Map<String, String> getEvalProcessState(final String processId) {
        Map<String, String> result = new HashMap<>();
        StateMachine<States, Events> machine = machineRepo.get(processId);
        if (machine != null) {
            if (States.FINISHED == machine.getState().getId()) {
                result.put("status", "ok");
                result.put("submission", EvalMachineFactory.extractSubmissionId(machine));
            } else {
                result.put("status", "pending");
            }
        } else {
            result.put("status", "unknown");
        }
        return result;
    }

    @Async("evalWorkerExecutor")
    public void fireEvalProcessExecutionAsync(final String processId) {
        StateMachine<States, Events> machine = machineRepo.get(processId);
        try {
            while (States.FINISHED != machine.getState().getId()) {
                machine = executeStep(machine);
                machineRepo.store(processId, machine);
            }
            logger.debug("Submission with id " + processId + " is finished.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private StateMachine<States, Events> executeStep(StateMachine<States, Events> machine) throws InterruptedException {
        String step = EvalMachineFactory.extractProcessStep(machine);
        int stepDelayInS = EvalMachineFactory.extractProcessStepDelayInS(machine);
        String submissionId = EvalMachineFactory.extractSubmissionId(machine);

        logger.debug(String.format("Current submission %s in state %s", submissionId, machine.getState().getId()));
        logger.debug(String.format("Execute step %s with delay of %d", step, stepDelayInS));

        if (stepDelayInS > 0) {
            logger.debug("Delaying ...");
            TimeUnit.SECONDS.sleep(Long.parseLong(stepDelayInS + ""));
        }

        ProcessStep stepObj = stepFactory.getStep(step);
        if (stepObj != null) {
            Events nextEvent = stepObj.execute(submissionId);
            machine.sendEvent(Mono.just(MessageBuilder.withPayload(nextEvent).build())).subscribe();
        }

        return machine;
    }

}
