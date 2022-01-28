package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.student.evaluation.process.step.DelegateCodeExecStep;
import ch.uzh.ifi.access.student.evaluation.process.step.GradeSubmissionStep;
import ch.uzh.ifi.access.student.evaluation.process.step.RouteSubmissionStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;

public class EvalMachineFactoryTest {

    private String submissionId = "sub1";

    private StateMachine<EvalMachine.States, EvalMachine.Events> stateMachine;

    @BeforeEach
    public void setUp() throws Exception {
        stateMachine = EvalMachineFactory.initSMForSubmission(submissionId);
        stateMachine.start();
    }

    @AfterEach
    public void tearDown() {
        stateMachine = null;
    }

    @Test
    public void initTest() {
        Assertions.assertEquals(EvalMachine.States.SUBMITTED,
                stateMachine.getState().getId());

        Assertions.assertNotNull(stateMachine);
        Assertions.assertEquals(RouteSubmissionStep.class.getName(),
                EvalMachineFactory.extractProcessStep(stateMachine));
    }

    @Test
    public void extendedState() {
        stateMachine.getExtendedState().getVariables().put("id", "test");

        Assertions.assertEquals("test",
                stateMachine.getExtendedState().getVariables().get("id"));
    }

    @Test
    public void grade() {
        stateMachine.sendEvent(EvalMachine.Events.GRADE);

        Assertions.assertEquals(EvalMachine.States.GRADING, stateMachine.getState().getId());
        Assertions.assertEquals(GradeSubmissionStep.class.getName(),
                EvalMachineFactory.extractProcessStep(stateMachine));
    }

    @Test
    public void delegate() {
        stateMachine.sendEvent(EvalMachine.Events.DELEGATE);

        Assertions.assertEquals(EvalMachine.States.DELEGATE, stateMachine.getState().getId());
        Assertions.assertEquals(DelegateCodeExecStep.class.getName(),
                EvalMachineFactory.extractProcessStep(stateMachine));
    }

    @Test
    public void returning() {
        stateMachine.sendEvent(EvalMachine.Events.DELEGATE);
        Assertions.assertEquals(EvalMachine.States.DELEGATE, stateMachine.getState().getId());
        Assertions.assertEquals(DelegateCodeExecStep.class.getName(),
                EvalMachineFactory.extractProcessStep(stateMachine));


        stateMachine.sendEvent(EvalMachine.Events.RETURN);
        Assertions.assertEquals(EvalMachine.States.RETURNING, stateMachine.getState().getId());
//        Assertions.assertThat(EvalMachineFactory.extractProcessStep(stateMachine))
//                .isEqualTo(DelegateCodeExecStep.class.getName());
    }

    @Test
    public void ignoreWrongEvent() {
        stateMachine.sendEvent(EvalMachine.Events.DELEGATE);
        Assertions.assertEquals(EvalMachine.States.DELEGATE,
                stateMachine.getState().getId());

        stateMachine.sendEvent(EvalMachine.Events.GRADE);
        Assertions.assertEquals(EvalMachine.States.DELEGATE,
                stateMachine.getState().getId());
    }

}
