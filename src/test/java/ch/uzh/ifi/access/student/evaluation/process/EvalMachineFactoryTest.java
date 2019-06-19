package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachineFactory;
import ch.uzh.ifi.access.student.evaluation.process.step.DelegateCodeExecStep;
import ch.uzh.ifi.access.student.evaluation.process.step.GradeSubmissionStep;
import ch.uzh.ifi.access.student.evaluation.process.step.RouteSubmissionStep;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.statemachine.StateMachine;

public class EvalMachineFactoryTest {

    private String submissionId = "sub1";

    private StateMachine stateMachine;

    @Before
    public void setUp() throws Exception {
        stateMachine = EvalMachineFactory.initSMForSubmission(submissionId);
        stateMachine.start();
    }

    @After
    public void tearDown() throws Exception {
        stateMachine = null;
    }

    @Test
    public void initTest() {
        Assertions.assertThat(stateMachine.getState().getId())
                .isEqualTo(EvalMachine.States.SUBMITTED);

        Assertions.assertThat(stateMachine).isNotNull();
        Assertions.assertThat(EvalMachineFactory.extractProcessStep(stateMachine))
                .isEqualTo(RouteSubmissionStep.class.getName());
    }

    @Test
    public void extendedState() {
        stateMachine.getExtendedState().getVariables().put("id", "test");

        Assertions.assertThat(stateMachine.getExtendedState().getVariables().get("id"))
                .isEqualTo("test");
    }

    @Test
    public void grade() {
        stateMachine.sendEvent(EvalMachine.Events.GRADE);

        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(EvalMachine.States.GRADING);
        Assertions.assertThat(EvalMachineFactory.extractProcessStep(stateMachine))
                .isEqualTo(GradeSubmissionStep.class.getName());
    }

    @Test
    public void delegate() {
        stateMachine.sendEvent(EvalMachine.Events.DELEGATE);

        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(EvalMachine.States.DELEGATE);
        Assertions.assertThat(EvalMachineFactory.extractProcessStep(stateMachine))
                .isEqualTo(DelegateCodeExecStep.class.getName());
    }

    @Test
    public void returning() {
        stateMachine.sendEvent(EvalMachine.Events.DELEGATE);
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(EvalMachine.States.DELEGATE);
        Assertions.assertThat(EvalMachineFactory.extractProcessStep(stateMachine))
                .isEqualTo(DelegateCodeExecStep.class.getName());


        stateMachine.sendEvent(EvalMachine.Events.RETURN);
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(EvalMachine.States.RETURNING);
//        Assertions.assertThat(EvalMachineFactory.extractProcessStep(stateMachine))
//                .isEqualTo(DelegateCodeExecStep.class.getName());
    }

    @Test
    public void ignoreWrongEvent() {
        stateMachine.sendEvent(EvalMachine.Events.DELEGATE);
        Assertions.assertThat(stateMachine.getState().getId())
                .isEqualTo(EvalMachine.States.DELEGATE);

        stateMachine.sendEvent(EvalMachine.Events.GRADE);
        Assertions.assertThat(stateMachine.getState().getId())
                .isEqualTo(EvalMachine.States.DELEGATE);
    }

}
