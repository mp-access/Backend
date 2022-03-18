package ch.uzh.ifi.access.course.event;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.config.AsyncConfig;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.event.BreakingChangeEventListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {AsyncConfig.class, BreakingChangeNotifier.class, BreakingChangeEventListener.class})
public class BreakingChangeNotifierIntegrationTest {

    @Autowired
    private BreakingChangeNotifier notifier;

    @MockBean
    private BreakingChangeEventListener listener;

    @Test
    public void notifyBreakingChanges() {
        Exercise codeExercise = TestObjectFactory.createCodeExercise();
        Exercise textExercise = TestObjectFactory.createTextExercise();
        Exercise multipleChoiceExercise = TestObjectFactory.createMultipleChoiceExercise();

        List<Exercise> exercises = List.of(codeExercise, textExercise, multipleChoiceExercise);

        notifier.notifyBreakingChanges(exercises);

        Mockito.verify(listener).handleBreakingChangeEvent(any(BreakingChangeEvent.class));
    }
}