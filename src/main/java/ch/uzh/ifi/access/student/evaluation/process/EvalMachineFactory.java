package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine.Events;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachine.States;
import ch.uzh.ifi.access.student.evaluation.process.step.DelegateCodeExecStep;
import ch.uzh.ifi.access.student.evaluation.process.step.GradeSubmissionStep;
import ch.uzh.ifi.access.student.evaluation.process.step.RouteSubmissionStep;
import ch.uzh.ifi.access.student.evaluation.process.step.WaitForExecutedCodeStep;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;

import java.util.EnumSet;

public class EvalMachineFactory {

    public static final String EXTENDED_VAR_SUBMISSION_ID = "submissionId";
    public static final String EXTENDED_VAR_NEXT_STEP = "nextStep";
    public static final String EXTENDED_VAR_NEXT_STEP_DELAY = "nextStepDelay";
    public static final String EXTENDED_VAR_COMPLETION_TIME = "completionTime";
    public static final String EXTENDED_VAR_STARTED_TIME = "startedTime";

    public static StateMachine<States, Events> initSMForSubmission(String submissionId) throws Exception {

        StateMachineBuilder.Builder<States, Events> builder
                = StateMachineBuilder.builder();

        // @formatter:off
        builder.configureStates()
                .withStates()
                .initial(States.SUBMITTED, routeAction())
                .end(States.FINISHED)
                .states(EnumSet.allOf(States.class));

        builder.configureTransitions()
                .withExternal()
                .source(States.SUBMITTED).target(States.GRADING)
                .event(Events.GRADE)
                .action(gradeAction())
                .and()
                .withExternal()
                .source(States.SUBMITTED).target(States.DELEGATE)
                .event(Events.DELEGATE)
                .action(delegateAction())
                .and()
                .withExternal()
                .source(States.DELEGATE).target(States.RETURNING)
                .event(Events.RETURN)
                .action(waitForExecutedCodeAction())
                .and()
                .withExternal()
                .source(States.RETURNING).target(States.GRADING)
                .event(Events.GRADE)
                .action(gradeAction())
                .and()
                .withExternal()
                .source(States.GRADING).target(States.FINISHED)
                .event(Events.FINISH);
        //@formatter:on

        StateMachine<States, Events> machine = builder.build();
        machine.getExtendedState().getVariables().put(EXTENDED_VAR_SUBMISSION_ID, submissionId);
        machine.addStateListener(new StateMachineEventListener(machine));
        return machine;
    }

    public static String extractProcessStep(StateMachine<States, Events> machine) {
        return machine.getExtendedState().getVariables().get(EXTENDED_VAR_NEXT_STEP).toString();
    }

    public static int extractProcessStepDelayInS(StateMachine<States, Events> machine) {
        return Integer.parseInt(machine.getExtendedState().getVariables().get(EXTENDED_VAR_NEXT_STEP_DELAY).toString());
    }

    public static String extractSubmissionId(StateMachine<States, Events> machine) {
        return machine.getExtendedState().getVariables().get(EXTENDED_VAR_SUBMISSION_ID).toString();
    }

    private static Action<States, Events> routeAction() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, RouteSubmissionStep.class.getName());
            ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP_DELAY, "0");
        };
    }

    private static Action<States, Events> delegateAction() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, DelegateCodeExecStep.class.getName());
            ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP_DELAY, "0");
        };
    }

    private static Action<States, Events> gradeAction() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, GradeSubmissionStep.class.getName());
            ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP_DELAY, "0");
        };
    }

    private static Action<States, Events> waitForExecutedCodeAction() {
        return ctx -> {
            ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP, WaitForExecutedCodeStep.class.getName());
            ctx.getExtendedState().getVariables().put(EXTENDED_VAR_NEXT_STEP_DELAY, "0");
        };
    }
}
