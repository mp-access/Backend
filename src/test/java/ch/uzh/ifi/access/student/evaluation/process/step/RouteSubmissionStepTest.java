package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RouteSubmissionStepTest {

    @Mock
    private StudentSubmissionService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void executeCodeSubmission() {
        RouteSubmissionStep step = new RouteSubmissionStep(service);

        StudentSubmission submission = TestObjectFactory.createCodeAnswer();
        when(service.findById(anyString())).thenReturn(Optional.of(submission));

        EvalMachine.Events event = step.execute("");
        Assertions.assertThat(event).isEqualTo(EvalMachine.Events.DELEGATE);
    }

    @Test
    public void executeOtherSubmission() {
        RouteSubmissionStep step = new RouteSubmissionStep(service);

        StudentSubmission submission = TestObjectFactory.createTextAnswer();
        when(service.findById(anyString())).thenReturn(Optional.of(submission));

        EvalMachine.Events event = step.execute("");
        Assertions.assertThat(event).isEqualTo(EvalMachine.Events.GRADE);
    }
}