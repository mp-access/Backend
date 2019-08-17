package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.event.BreakingChangeEvent;
import ch.uzh.ifi.access.course.model.Exercise;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class BreakingChangeNotifierTest {

    @Test
    public void notifyBreakingChanges() {
        MockChangeNotifier mockNotifier = new MockChangeNotifier();
        BreakingChangeNotifier notifier = new BreakingChangeNotifier(mockNotifier);

        Exercise codeExercise = TestObjectFactory.createCodeExercise("");
        Exercise textExercise = TestObjectFactory.createTextExercise("");
        Exercise multipleChoiceExercise = TestObjectFactory.createMultipleChoiceExercise("");

        List<Exercise> exercises = List.of(codeExercise, textExercise, multipleChoiceExercise);
        notifier.notifyBreakingChanges(exercises);

        List<String> ids = mockNotifier.exercises;
        Assertions.assertThat(ids).containsExactly(codeExercise.getId(), textExercise.getId(), multipleChoiceExercise.getId());
    }

    @Test
    public void notifyNothing() {
        MockChangeNotifier mockNotifier = new MockChangeNotifier();
        BreakingChangeNotifier notifier = new BreakingChangeNotifier(mockNotifier);

        notifier.notifyBreakingChanges(List.of());
        Assertions.assertThat(mockNotifier.exercises).isNull();

        notifier.notifyBreakingChanges(null);
        Assertions.assertThat(mockNotifier.exercises).isNull();
    }

    private static class MockChangeNotifier implements ApplicationEventPublisher {

        private List<String> exercises;

        @Override
        public void publishEvent(Object event) {
            exercises = ((BreakingChangeEvent) event).getBreakingChangeExerciseIds();
        }
    }
}