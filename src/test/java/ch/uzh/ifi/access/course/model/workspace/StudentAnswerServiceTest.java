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

    @Test(expected = IllegalArgumentException.class)
    public void saveNullCodeSubmission() {
        service.saveSubmission(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveCodeSubmissionNoUserId() {
        CodeAnswer codeAnswer = new CodeAnswer();
        codeAnswer.setExerciseId("123");
        service.saveSubmission(codeAnswer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveCodeSubmissionNoExerciseId() {
        CodeAnswer codeAnswer = new CodeAnswer();
        codeAnswer.setUserId("123");
        service.saveSubmission(codeAnswer);
    }

    @Test
    public void saveCodeSubmission() {

        Exercise exercise = TestObjectFactory.createCodeExercise("Hello, world?");
        CodeAnswer submittedAnswer = TestObjectFactory.createCodeAnswer();
        submittedAnswer.setExercise(exercise);

        CodeAnswer savedAnswer = service.saveSubmission(submittedAnswer);

        Assertions.assertThat(savedAnswer).isNotNull();
        Assertions.assertThat(savedAnswer.getId()).isNotNull();
        Assertions.assertThat(savedAnswer.getVersion()).isEqualTo(0);
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

    @Test
    public void findAllByExerciseId() {
        repository.deleteAll();
        Exercise exercise = TestObjectFactory.createCodeExercise("Hello, world?");
        Exercise someOtherExercise = TestObjectFactory.createCodeExercise("Some other exercise");

        CodeAnswer codeSubmission1 = TestObjectFactory.createCodeAnswerWithExercise(exercise);

        CodeAnswer codeSubmission2 = TestObjectFactory.createCodeAnswerWithExercise(exercise);
        CodeAnswer codeSubmission3 = TestObjectFactory.createCodeAnswerWithExercise(exercise);
        CodeAnswer someOtherSubmission = TestObjectFactory.createCodeAnswerWithExercise(someOtherExercise);

        // Explicitly set user ids for test
        final String userId1 = "userId-1";
        final String userId2 = "userId-2";
        codeSubmission1.setUserId(userId1);
        codeSubmission2.setUserId(userId1);
        codeSubmission3.setUserId(userId2);
        someOtherSubmission.setUserId(userId2);

        codeSubmission1 = service.saveSubmission(codeSubmission1);
        codeSubmission2 = service.saveSubmission(codeSubmission2);
        codeSubmission3 = service.saveSubmission(codeSubmission3);
        someOtherSubmission = service.saveSubmission(someOtherSubmission);

        List<CodeAnswer> answers = service.findAllSubmissionsOrderedByVersionDesc(exercise.getId(), userId1);

        Assertions.assertThat(answers.size()).isEqualTo(2);

        Assertions.assertThat(answers.get(1).getId()).isEqualTo(codeSubmission1.getId());
        Assertions.assertThat(answers.get(1).getExerciseId()).isEqualTo(codeSubmission1.getExerciseId());
        Assertions.assertThat(answers.get(1).getVersion()).isEqualTo(codeSubmission1.getVersion());
        Assertions.assertThat(answers.get(1).getVersion()).isEqualTo(0);

        Assertions.assertThat(answers.get(0).getId()).isEqualTo(codeSubmission2.getId());
        Assertions.assertThat(answers.get(0).getExerciseId()).isEqualTo(codeSubmission2.getExerciseId());
        Assertions.assertThat(answers.get(0).getVersion()).isEqualTo(codeSubmission2.getVersion());
        Assertions.assertThat(answers.get(0).getVersion()).isEqualTo(1);

        answers = service.findAllSubmissionsOrderedByVersionDesc(exercise.getId(), userId2);
        Assertions.assertThat(answers.size()).isEqualTo(1);

        Assertions.assertThat(answers.get(0).getId()).isEqualTo(codeSubmission3.getId());
        Assertions.assertThat(answers.get(0).getExerciseId()).isEqualTo(codeSubmission3.getExerciseId());
        Assertions.assertThat(answers.get(0).getVersion()).isEqualTo(codeSubmission3.getVersion());

        answers = service.findAllSubmissionsOrderedByVersionDesc(someOtherExercise.getId(), userId2);
        Assertions.assertThat(answers.size()).isEqualTo(1);

        Assertions.assertThat(answers.get(0).getId()).isEqualTo(someOtherSubmission.getId());
        Assertions.assertThat(answers.get(0).getExerciseId()).isEqualTo(someOtherSubmission.getExerciseId());
        Assertions.assertThat(answers.get(0).getVersion()).isEqualTo(someOtherSubmission.getVersion());
    }
}