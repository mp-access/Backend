package ch.uzh.ifi.access.student.evaluation;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachineFactory;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachineRepoService;
import ch.uzh.ifi.access.student.evaluation.process.ProcessStepFactoryService;
import ch.uzh.ifi.access.student.evaluation.process.step.ProcessStep;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public String initEvalProcess(StudentSubmission submission){
        String processId = UUID.randomUUID().toString();
        StateMachine m = null;
        try {
            m = EvalMachineFactory.initSMForSubmission(submission.getId());
            m.start();
            machineRepo.store(processId, m);
        } catch (Exception e) {
            logger.error("Could not create state machine for submission: "+ submission.getId());
            logger.error(e.getMessage());
        }

        return processId;
    }

    public void processSubmissionStep(String submissionId){

        StateMachine machine = machineRepo.get(submissionId);

        if(EvalMachine.States.FINISHED != machine.getState().getId()) {
            String step = EvalMachineFactory.extractProcessStep(machine);
            logger.debug("current step: " + step);
            EvalMachine.Events nextEvent = stepFactory.getStep(step).execute(submissionId);
            machine.sendEvent(nextEvent);
            machineRepo.store(submissionId, machine);
        }else{
            logger.debug("Submission with id "+ submissionId +" is finished.");
        }

    }

    private ProcessStep getStepForMachine(){
        return null;
    }
}
