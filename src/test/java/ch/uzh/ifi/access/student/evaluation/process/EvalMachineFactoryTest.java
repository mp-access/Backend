package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.student.evaluation.process.step.DelegateCodeExecStep;
import ch.uzh.ifi.access.student.evaluation.process.step.GradeSubmissionStep;
import ch.uzh.ifi.access.student.evaluation.process.step.RouteSubmissionStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import reactor.core.publisher.Mono;

class EvalMachineFactoryTest {

    private StateMachine<EvalMachine.States, EvalMachine.Events> stateMachine;

    @BeforeEach
    void setUp() throws Exception {
        stateMachine = EvalMachineFactory.initSMForSubmission("sub1");
        stateMachine.startReactively().subscribe();
    }

    @AfterEach
    void tearDown() {
        stateMachine = null;
    }

    @Test
    void initTest() {
        Assertions.assertEquals(EvalMachine.States.SUBMITTED,
                stateMachine.getState().getId());

        Assertions.assertNotNull(stateMachine);
        Assertions.assertEquals(RouteSubmissionStep.class.getName(),
                EvalMachineFactory.extractProcessStep(stateMachine));
    }

    @Test
    void extendedState() {
        stateMachine.getExtendedState().getVariables().put("id", "test");

        Assertions.assertEquals("test",
                stateMachine.getExtendedState().getVariables().get("id"));
    }

    @Test
    void grade() {
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(EvalMachine.Events.GRADE).build())).subscribe();

        Assertions.assertEquals(EvalMachine.States.GRADING, stateMachine.getState().getId());
        Assertions.assertEquals(GradeSubmissionStep.class.getName(),
                EvalMachineFactory.extractProcessStep(stateMachine));
    }

    @Test
    void delegate() {
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(EvalMachine.Events.DELEGATE).build())).subscribe();

        Assertions.assertEquals(EvalMachine.States.DELEGATE, stateMachine.getState().getId());
        Assertions.assertEquals(DelegateCodeExecStep.class.getName(),
                EvalMachineFactory.extractProcessStep(stateMachine));
    }

    @Test
    void returning() {
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(EvalMachine.Events.DELEGATE).build())).subscribe();
        Assertions.assertEquals(EvalMachine.States.DELEGATE, stateMachine.getState().getId());
        Assertions.assertEquals(DelegateCodeExecStep.class.getName(),
                EvalMachineFactory.extractProcessStep(stateMachine));


        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(EvalMachine.Events.RETURN).build())).subscribe();
        Assertions.assertEquals(EvalMachine.States.RETURNING, stateMachine.getState().getId());
//        Assertions.assertThat(EvalMachineFactory.extractProcessStep(stateMachine))
//                .isEqualTo(DelegateCodeExecStep.class.getName());
    }

    @Test
    void ignoreWrongEvent() {
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(EvalMachine.Events.DELEGATE).build())).subscribe();
        Assertions.assertEquals(EvalMachine.States.DELEGATE,
                stateMachine.getState().getId());

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(EvalMachine.Events.GRADE).build())).subscribe();
        Assertions.assertEquals(EvalMachine.States.DELEGATE,
                stateMachine.getState().getId());
    }

}
