package ch.uzh.ifi.access.student.evaluation;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.model.workspace.TextSubmission;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.EnumSet;

public class EvalMachineBuilderTest {

    private EvalActionHandler actionHandler;

    private StateMachine stateMachine;

    @Before
    public void setup() throws Exception {

        actionHandler = new EvalActionHandler();

        StateMachineBuilder.Builder<EvalMachine.States, EvalMachine.Events> builder
                = StateMachineBuilder.builder();

        builder.configureStates().withStates()
                .initial(EvalMachine.States.SUBMITTED)
                .end(EvalMachine.States.GRADED)
                .states(EnumSet.allOf(EvalMachine.States.class));

        builder.configureTransitions()
                .withExternal()
                .source(EvalMachine.States.SUBMITTED).target(EvalMachine.States.GRADED)
                .event(EvalMachine.Events.GRADE)
                .action(actionHandler.grade())
                .and()
                .withExternal()
                .source(EvalMachine.States.SUBMITTED).target(EvalMachine.States.DELEGATED)
                .event(EvalMachine.Events.DELEGATE)
                .and()
                .withExternal()
                .source(EvalMachine.States.DELEGATED).target(EvalMachine.States.RETURNED)
                .event(EvalMachine.Events.RETURN);

        stateMachine = builder.build();
        stateMachine.start();
    }

    @Test
    public void initTest() {
        Assertions.assertThat(stateMachine.getState().getId())
                .isEqualTo(EvalMachine.States.SUBMITTED);

        Assertions.assertThat(stateMachine).isNotNull();
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

        Assertions.assertThat(stateMachine.getState().getId())
                .isEqualTo(EvalMachine.States.GRADED);
    }


    @Test
    public void ignoreWrongEvent() {
        stateMachine.sendEvent(EvalMachine.Events.DELEGATE);
        Assertions.assertThat(stateMachine.getState().getId())
                .isEqualTo(EvalMachine.States.DELEGATED);

        stateMachine.sendEvent(EvalMachine.Events.GRADE);
        Assertions.assertThat(stateMachine.getState().getId())
                .isEqualTo(EvalMachine.States.DELEGATED);
    }

    @Test
    public void gradeTextSubmission() {

        TextSubmission sub = TextSubmission.builder()
                .id("1")
                .answer("something")
                .exercise(Exercise.builder().type(ExerciseType.text).build())
                .build();

        actionHandler.storeSubmission(sub);

        stateMachine.getExtendedState().getVariables().put("id", sub.getId());

        stateMachine.sendEvent(EvalMachine.Events.GRADE);

        Assertions.assertThat(stateMachine.getState().getId())
                .isEqualTo(EvalMachine.States.GRADED);

        Assertions.assertThat(actionHandler.getSubmission(sub.getId()).getResult().getScore())
                .isEqualTo(0);
    }
}
