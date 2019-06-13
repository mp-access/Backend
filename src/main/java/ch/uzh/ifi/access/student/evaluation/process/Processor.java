package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.student.evaluation.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.process.step.ProcessStep;
import org.springframework.statemachine.StateMachine;

public class Processor {

    public void processStep(StateMachine machine){
        String submissionId = machine.getExtendedState().getVariables().get("id").toString();

        ProcessStep step = getStepForMachine();
        EvalMachine.Events nextEvent = step.execute(submissionId);
        machine.sendEvent(nextEvent);

    }

    private ProcessStep getStepForMachine(){
        return null;
    }
}
