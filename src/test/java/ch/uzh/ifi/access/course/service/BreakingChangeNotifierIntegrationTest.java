package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.config.AsyncConfig;
import ch.uzh.ifi.access.course.event.BreakingChangeEvent;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.event.BreakingChangeEventListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {AsyncConfig.class, BreakingChangeNotifier.class, BreakingChangeEventListener.class})
@RunWith(SpringRunner.class)
public class BreakingChangeNotifierIntegrationTest {

    @Autowired
    private BreakingChangeNotifier notifier;

    @MockBean
    private BreakingChangeEventListener listener;

    @Test
    public void notifyBreakingChanges() {
        Exercise codeExercise = TestObjectFactory.createCodeExercise("");
        Exercise textExercise = TestObjectFactory.createTextExercise("");
        Exercise multipleChoiceExercise = TestObjectFactory.createMultipleChoiceExercise("");

        List<Exercise> exercises = List.of(codeExercise, textExercise, multipleChoiceExercise);

        notifier.notifyBreakingChanges(exercises);

        Mockito.verify(listener).handleBreakingChangeEvent(any(BreakingChangeEvent.class));
    }
}