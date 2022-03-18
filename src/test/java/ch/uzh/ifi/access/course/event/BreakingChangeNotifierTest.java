package ch.uzh.ifi.access.course.event;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Exercise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

public class BreakingChangeNotifierTest {

    @Test
    public void notifyBreakingChanges() {
        MockChangeNotifier mockNotifier = new MockChangeNotifier();
        BreakingChangeNotifier notifier = new BreakingChangeNotifier(mockNotifier);

        Exercise codeExercise = TestObjectFactory.createCodeExercise();
        Exercise textExercise = TestObjectFactory.createTextExercise();
        Exercise multipleChoiceExercise = TestObjectFactory.createMultipleChoiceExercise();

        List<Exercise> exercises = List.of(codeExercise, textExercise, multipleChoiceExercise);
        notifier.notifyBreakingChanges(exercises);

        List<String> ids = mockNotifier.exercises;
        Assertions.assertEquals(
                Arrays.asList(codeExercise.getId(), textExercise.getId(), multipleChoiceExercise.getId()), ids);
    }

    @Test
    public void notifyNothing() {
        MockChangeNotifier mockNotifier = new MockChangeNotifier();
        BreakingChangeNotifier notifier = new BreakingChangeNotifier(mockNotifier);

        notifier.notifyBreakingChanges(List.of());
        Assertions.assertNull(mockNotifier.exercises);

        notifier.notifyBreakingChanges(null);
        Assertions.assertNull(mockNotifier.exercises);
    }

    private static class MockChangeNotifier implements ApplicationEventPublisher {

        private List<String> exercises;

        @Override
        public void publishEvent(@NotNull Object event) {
            exercises = ((BreakingChangeEvent) event).getBreakingChangeExerciseIds();
        }
    }
}