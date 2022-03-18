package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.SubmissionProperties;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.runner.SubmissionCodeRunner;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DataMongoTest
public class DelegateCodeExecStepTest {

    @Autowired
    private StudentSubmissionRepository repository;

    @Mock
    private SubmissionCodeRunner codeRunner;

    @Mock
    private CourseDAO courseDAO;

    private StudentSubmissionService submissionService;

    private CourseService courseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        submissionService = new StudentSubmissionService(repository, new SubmissionProperties());
        courseService = new CourseService(courseDAO, null);
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void execute() throws Exception {
        DelegateCodeExecStep execStep = new DelegateCodeExecStep(submissionService, courseService, codeRunner);

        final String exerciseId = "ex";
        final String submissionId = "id";
        CodeSubmission studentSubmission = TestObjectFactory.createCodeAnswer("", exerciseId);
        studentSubmission.setId(submissionId);
        studentSubmission = repository.save(studentSubmission);

        ExecResult result = new ExecResult("Hello, stdout", "Hello, stderr", "Hello, private test log", null, null);
        when(codeRunner.execSubmissionForExercise(any(CodeSubmission.class), any(Exercise.class))).thenReturn(result);
        when(courseService.getExerciseById(anyString())).thenReturn(Optional.of(TestObjectFactory.createCodeExercise()));

        EvalMachine.Events execute = execStep.execute(studentSubmission.getId());
        Assertions.assertEquals(execute, EvalMachine.Events.RETURN);

        studentSubmission = (CodeSubmission) repository.findById(studentSubmission.getId()).orElseThrow();
        Assertions.assertEquals(studentSubmission.getConsole(), result);
    }
}