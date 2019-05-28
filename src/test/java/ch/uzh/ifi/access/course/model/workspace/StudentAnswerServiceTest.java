package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Exercise;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@DataMongoTest
@RunWith(SpringRunner.class)
public class StudentAnswerServiceTest {

    @Autowired
    private StudentAnswerRepository repository;

    private StudentAnswerService service;

    @Before
    public void setUp() {
        this.service = new StudentAnswerService(repository);
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void saveCodeSubmission() {

        Exercise exercise = TestObjectFactory.createCodeExercise("Hello, world?");
        CodeAnswer submittedAnswer = TestObjectFactory.createCodeAnswer();
        submittedAnswer.setExercise(exercise);

        CodeAnswer savedAnswer = service.saveSubmission(submittedAnswer);

        Assertions.assertThat(savedAnswer).isNotNull();
        Assertions.assertThat(savedAnswer.getId()).isNotNull();
        Assertions.assertThat(savedAnswer.getVersion()).isEqualTo(submittedAnswer.getVersion());
        Assertions.assertThat(savedAnswer.getUserId()).isEqualTo(submittedAnswer.getUserId());
        Assertions.assertThat(savedAnswer.getCommitId()).isEqualTo(submittedAnswer.getCommitId());
        Assertions.assertThat(savedAnswer.getCourseId()).isEqualTo(submittedAnswer.getCourseId());
        Assertions.assertThat(savedAnswer.getAssignmentId()).isEqualTo(submittedAnswer.getAssignmentId());
        Assertions.assertThat(savedAnswer.getExerciseId()).isEqualTo(submittedAnswer.getExerciseId());
        Assertions.assertThat(savedAnswer.getTimestamp()).isEqualTo(submittedAnswer.getTimestamp());
    }

    @Test
    public void saveTextSubmission() {
        Exercise exercise = TestObjectFactory.createTextExercise("Hello, world?");
        TextAnswer submittedAnswer = TestObjectFactory.createTextAnswer();
        submittedAnswer.setExercise(exercise);

        TextAnswer savedAnswer = service.saveSubmission(submittedAnswer);
        Assertions.assertThat(savedAnswer).isNotNull();
        Assertions.assertThat(savedAnswer.getId()).isNotNull();
        Assertions.assertThat(savedAnswer.getVersion()).isEqualTo(submittedAnswer.getVersion());
        Assertions.assertThat(savedAnswer.getUserId()).isEqualTo(submittedAnswer.getUserId());
        Assertions.assertThat(savedAnswer.getCommitId()).isEqualTo(submittedAnswer.getCommitId());
        Assertions.assertThat(savedAnswer.getCourseId()).isEqualTo(submittedAnswer.getCourseId());
        Assertions.assertThat(savedAnswer.getAssignmentId()).isEqualTo(submittedAnswer.getAssignmentId());
        Assertions.assertThat(savedAnswer.getExerciseId()).isEqualTo(submittedAnswer.getExerciseId());
        Assertions.assertThat(savedAnswer.getTimestamp()).isEqualTo(submittedAnswer.getTimestamp());
    }

    @Test
    public void saveMultipleChoiceSubmission() {
        Exercise exercise = TestObjectFactory.createMultipleChoiceExercise("Hello, world?");
        MultipleChoiceAnswer submittedAnswer = TestObjectFactory.createMultipleChoiceAnswer();
        submittedAnswer.setExercise(exercise);

        MultipleChoiceAnswer savedAnswer = service.saveSubmission(submittedAnswer);
        Assertions.assertThat(savedAnswer).isNotNull();
        Assertions.assertThat(savedAnswer.getId()).isNotNull();
        Assertions.assertThat(savedAnswer.getVersion()).isEqualTo(submittedAnswer.getVersion());
        Assertions.assertThat(savedAnswer.getUserId()).isEqualTo(submittedAnswer.getUserId());
        Assertions.assertThat(savedAnswer.getCommitId()).isEqualTo(submittedAnswer.getCommitId());
        Assertions.assertThat(savedAnswer.getCourseId()).isEqualTo(submittedAnswer.getCourseId());
        Assertions.assertThat(savedAnswer.getAssignmentId()).isEqualTo(submittedAnswer.getAssignmentId());
        Assertions.assertThat(savedAnswer.getExerciseId()).isEqualTo(submittedAnswer.getExerciseId());
        Assertions.assertThat(savedAnswer.getTimestamp()).isEqualTo(submittedAnswer.getTimestamp());
    }

    @Test
    public void findAllSubmissions() {
        Exercise codeExercise = TestObjectFactory.createCodeExercise("Hello, world?");
        CodeAnswer codeSubmission = TestObjectFactory.createCodeAnswer();
        codeSubmission.setExercise(codeExercise);

        Exercise textExercise = TestObjectFactory.createTextExercise("Hello, world?");
        TextAnswer textSubmission = TestObjectFactory.createTextAnswer();
        textSubmission.setExercise(textExercise);

        Exercise multipleChoiceExercise = TestObjectFactory.createMultipleChoiceExercise("Hello, world?");
        MultipleChoiceAnswer multipleChoiceSubmission = TestObjectFactory.createMultipleChoiceAnswer();
        multipleChoiceSubmission.setExercise(multipleChoiceExercise);

        codeSubmission = service.saveSubmission(codeSubmission);
        textSubmission = service.saveSubmission(textSubmission);
        multipleChoiceSubmission = service.saveSubmission(multipleChoiceSubmission);

        List<StudentAnswer> submissions = service.findAll();
        List<String> submissionIds = submissions.stream().map(StudentAnswer::getId).collect(Collectors.toList());

        Assertions.assertThat(submissionIds).isEqualTo(List.of(codeSubmission.getId(), textSubmission.getId(), multipleChoiceSubmission.getId()));

    }
}