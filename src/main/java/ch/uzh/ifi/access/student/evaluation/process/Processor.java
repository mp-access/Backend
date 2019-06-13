package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.student.evaluation.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.process.step.ProcessStep;
import ch.uzh.ifi.access.student.evaluation.process.step.ProcessStepFactory;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
public class Processor {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    private ProcessStepFactory stepFactory;

    private EvalMachineRepo machineRepo;

    @Autowired
    public Processor(ProcessStepFactory stepFactory, EvalMachineRepo machineRepo) {
        this.stepFactory = stepFactory;
        this.machineRepo = machineRepo;
    }

    public void startEvalProcess(StudentSubmission submission){
        try {
            StateMachine m = EvalMachineFactory.initSMForSubmission(submission.getId());
            m.start();
            machineRepo.store(submission.getId(), m);
        } catch (Exception e) {
            logger.error("Could not create state machine for submission: "+ submission.getId());
            logger.error(e.getMessage());
        }
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
