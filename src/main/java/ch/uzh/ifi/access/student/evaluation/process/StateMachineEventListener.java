package ch.uzh.ifi.access.student.evaluation.process;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.time.Instant;

public class StateMachineEventListener
        extends StateMachineListenerAdapter<EvalMachine.States, EvalMachine.Events> {

    private StateMachine<EvalMachine.States, EvalMachine.Events> machine;

    public StateMachineEventListener(StateMachine<EvalMachine.States, EvalMachine.Events> machine) {
        this.machine = machine;
    }

    @Override
    public void stateEntered(State<EvalMachine.States, EvalMachine.Events> state) {
        if (EvalMachine.States.SUBMITTED.equals(state.getId())) {
            machine.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_STARTED_TIME, Instant.now());
        }

        if (EvalMachine.States.FINISHED.equals(state.getId())) {
            machine.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now());
        }
    }
}