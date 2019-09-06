package ch.uzh.ifi.access.config.statemachine;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.process.step.DelegateCodeExecStep;
import ch.uzh.ifi.access.student.evaluation.process.step.GradeSubmissionStep;
import ch.uzh.ifi.access.student.evaluation.process.step.RouteSubmissionStep;
import ch.uzh.ifi.access.student.evaluation.process.step.WaitForExecutedCodeStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class EvalMachineConfig extends EnumStateMachineConfigurerAdapter<EvalMachine.States, EvalMachine.Events> {

    public static final String EXTENDED_VAR_SUBMISSION_ID = "submissionId";
    public static final String EXTENDED_VAR_USER = "user";
    public static final String EXTENDED_VAR_NEXT_STEP = "nextStep";
    public static final String EXTENDED_VAR_NEXT_STEP_DELAY = "nextStepDelay";

    @Autowired
    private StateMachineRuntimePersister<EvalMachine.States, EvalMachine.Events, String> stateMachineRuntimePersister;

    @Override
    public void configure(StateMachineConfigurationConfigurer<EvalMachine.States, EvalMachine.Events> config) throws Exception {
        config
            .withPersistence()
            .runtimePersister(stateMachineRuntimePersister);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EvalMachine.States, EvalMachine.Events> states)
            throws Exception {
        states
            .withStates()
            .initial(EvalMachine.States.SUBMITTED, routeAction())
            .end(EvalMachine.States.FINISHED)
            .states(EnumSet.allOf(EvalMachine.States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EvalMachine.States, EvalMachine.Events> transitions)
            throws Exception {
        transitions
            .withExternal()
            .source(EvalMachine.States.SUBMITTED).target(EvalMachine.States.GRADING)
            .event(EvalMachine.Events.GRADE)
            .action(gradeAction())
            .and()
            .withExternal()
            .source(EvalMachine.States.SUBMITTED).target(EvalMachine.States.DELEGATE)
            .event(EvalMachine.Events.DELEGATE)
            .action(delegateAction())
            .and()
            .withExternal()
            .source(EvalMachine.States.DELEGATE).target(EvalMachine.States.RETURNING)
            .event(EvalMachine.Events.RETURN)
            .action(waitForExecutedCodeAction())
            .and()
            .withExternal()
            .source(EvalMachine.States.RETURNING).target(EvalMachine.States.GRADING)
            .event(EvalMachine.Events.GRADE)
            .action(gradeAction())
            .and()
            .withExternal()
            .source(EvalMachine.States.GRADING).target(EvalMachine.States.FINISHED)
            .event(EvalMachine.Events.FINISH);
    }

    public static String extractProcessStep(StateMachine machine) {
        return machine.getExtendedState().getVariables().get(EXTENDED_VAR_NEXT_STEP).toString();
    }

    public static int extractProcessStepDelayInS(StateMachine machine) {
        return Integer.parseInt(machine.getExtendedState().getVariables().get(EXTENDED_VAR_NEXT_STEP_DELAY).toString());
    }

    public static String extractSubmissionId(StateMachine machine) {
        return machine.getExtendedState().getVariables().get(EXTENDED_VAR_SUBMISSION_ID).toString();
    }

    private static Action<EvalMachine.States, EvalMachine.Events> routeAction() {
        return new Action<EvalMachine.States, EvalMachine.Events>() {

            @Override
            public void execute(StateContext<EvalMachine.States, EvalMachine.Events> ctx) {
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, RouteSubmissionStep.class.getName());
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP_DELAY, "0");
            }
        };
    }

    private static Action<EvalMachine.States, EvalMachine.Events> delegateAction() {
        return new Action<EvalMachine.States, EvalMachine.Events>() {

            @Override
            public void execute(StateContext<EvalMachine.States, EvalMachine.Events> ctx) {
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, DelegateCodeExecStep.class.getName());
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP_DELAY, "0");
            }
        };
    }

    private static Action<EvalMachine.States, EvalMachine.Events> gradeAction() {
        return new Action<EvalMachine.States, EvalMachine.Events>() {

            @Override
            public void execute(StateContext<EvalMachine.States, EvalMachine.Events> ctx) {
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, GradeSubmissionStep.class.getName());
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP_DELAY, "0");
            }
        };
    }

    private static Action<EvalMachine.States, EvalMachine.Events> waitForExecutedCodeAction() {
        return new Action<EvalMachine.States, EvalMachine.Events>() {

            @Override
            public void execute(StateContext<EvalMachine.States, EvalMachine.Events> ctx) {
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, WaitForExecutedCodeStep.class.getName());
                ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP_DELAY, "0");
            }
        };
    }

}
