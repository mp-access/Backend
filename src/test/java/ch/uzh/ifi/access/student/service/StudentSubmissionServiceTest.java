package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@DataMongoTest
@RunWith(SpringRunner.class)
public class StudentSubmissionServiceTest {

    @Autowired
    private StudentSubmissionRepository repository;

    private StudentSubmissionService service;

    @Before
    public void setUp() {
        this.service = new StudentSubmissionService(repository);
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void findById() {
        CodeSubmission submission = new CodeSubmission();
        submission.setExerciseId("123");
        submission.setTimestamp(Instant.now());
        submission = repository.save(submission);

        Optional<? extends StudentSubmission> foundById = service.findById(submission.getId());
        Assertions.assertThat(foundById).isPresent();
        Assertions.assertThat(foundById.map(StudentSubmission::getExerciseId)).isEqualTo(submission.getExerciseId());
        Assertions.assertThat(foundById.map(StudentSubmission::getTimestamp)).isEqualTo(submission.getTimestamp());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByIdNull() {
        service.findById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveNullCodeSubmission() {
        service.saveSubmission(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveCodeSubmissionNoUserId() {
        CodeSubmission codeSubmission = new CodeSubmission();
        codeSubmission.setExerciseId("123");
        service.saveSubmission(codeSubmission);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveCodeSubmissionNoExerciseId() {
        CodeSubmission codeSubmission = new CodeSubmission();
        codeSubmission.setUserId("123");
        service.saveSubmission(codeSubmission);
    }

    @Test
    public void saveCodeSubmission() {
        Exercise exercise = TestObjectFactory.createCodeExercise("Hello, world?");
        CodeSubmission submittedAnswer = TestObjectFactory.createCodeAnswerWithExercise(exercise.getId());

        CodeSubmission savedAnswer = service.saveSubmission(submittedAnswer);

        Assertions.assertThat(savedAnswer).isNotNull();
        Assertions.assertThat(savedAnswer.getId()).isNotNull();
        Assertions.assertThat(savedAnswer.getVersion()).isEqualTo(0);
        Assertions.assertThat(savedAnswer.getUserId()).isEqualTo(submittedAnswer.getUserId());
        Assertions.assertThat(savedAnswer.getCommitId()).isEqualTo(submittedAnswer.getCommitId());
        Assertions.assertThat(savedAnswer.getExerciseId()).isEqualTo(submittedAnswer.getExerciseId());
        Assertions.assertThat(savedAnswer.getTimestamp()).isEqualTo(submittedAnswer.getTimestamp());
    }

    @Test
    public void saveTextSubmission() {
        Exercise exercise = TestObjectFactory.createTextExercise("Hello, world?");
        TextSubmission submittedAnswer = TestObjectFactory.createTextAnswerWithExercise(exercise.getId());

        TextSubmission savedAnswer = service.saveSubmission(submittedAnswer);
        Assertions.assertThat(savedAnswer).isNotNull();
        Assertions.assertThat(savedAnswer.getId()).isNotNull();
        Assertions.assertThat(savedAnswer.getVersion()).isEqualTo(submittedAnswer.getVersion());
        Assertions.assertThat(savedAnswer.getUserId()).isEqualTo(submittedAnswer.getUserId());
        Assertions.assertThat(savedAnswer.getCommitId()).isEqualTo(submittedAnswer.getCommitId());
        Assertions.assertThat(savedAnswer.getExerciseId()).isEqualTo(submittedAnswer.getExerciseId());
        Assertions.assertThat(savedAnswer.getTimestamp()).isEqualTo(submittedAnswer.getTimestamp());
    }

    @Test
    public void saveMultipleChoiceSubmission() {
        Exercise exercise = TestObjectFactory.createMultipleChoiceExercise("Hello, world?");
        MultipleChoiceSubmission submittedAnswer = TestObjectFactory.createMultipleChoiceAnswerWithExercise(exercise.getId());

        MultipleChoiceSubmission savedAnswer = service.saveSubmission(submittedAnswer);
        Assertions.assertThat(savedAnswer).isNotNull();
        Assertions.assertThat(savedAnswer.getId()).isNotNull();
        Assertions.assertThat(savedAnswer.getVersion()).isEqualTo(submittedAnswer.getVersion());
        Assertions.assertThat(savedAnswer.getUserId()).isEqualTo(submittedAnswer.getUserId());
        Assertions.assertThat(savedAnswer.getCommitId()).isEqualTo(submittedAnswer.getCommitId());
        Assertions.assertThat(savedAnswer.getExerciseId()).isEqualTo(submittedAnswer.getExerciseId());
        Assertions.assertThat(savedAnswer.getTimestamp()).isEqualTo(submittedAnswer.getTimestamp());
    }

    @Test
    public void findAllSubmissions() {
        Exercise codeExercise = TestObjectFactory.createCodeExercise("Hello, world?");
        CodeSubmission codeSubmission = TestObjectFactory.createCodeAnswerWithExercise(codeExercise.getId());

        Exercise textExercise = TestObjectFactory.createTextExercise("Hello, world?");
        TextSubmission textSubmission = TestObjectFactory.createTextAnswerWithExercise(textExercise.getId());

        Exercise multipleChoiceExercise = TestObjectFactory.createMultipleChoiceExercise("Hello, world?");
        MultipleChoiceSubmission multipleChoiceSubmission = TestObjectFactory.createMultipleChoiceAnswerWithExercise(multipleChoiceExercise.getId());

        codeSubmission = service.saveSubmission(codeSubmission);
        textSubmission = service.saveSubmission(textSubmission);
        multipleChoiceSubmission = service.saveSubmission(multipleChoiceSubmission);

        List<StudentSubmission> submissions = service.findAll();
        List<String> submissionIds = submissions.stream().map(StudentSubmission::getId).collect(Collectors.toList());

        Assertions.assertThat(submissionIds).isEqualTo(List.of(codeSubmission.getId(), textSubmission.getId(), multipleChoiceSubmission.getId()));
    }

    @Test
    public void findAllByExerciseId() {
        repository.deleteAll();
        Exercise exercise = TestObjectFactory.createCodeExercise("Hello, world?");
        Exercise someOtherExercise = TestObjectFactory.createCodeExercise("Some other exercise");

        CodeSubmission codeSubmission1 = TestObjectFactory.createCodeAnswerWithExercise(exercise.getId());

        CodeSubmission codeSubmission2 = TestObjectFactory.createCodeAnswerWithExercise(exercise.getId());
        CodeSubmission codeSubmission3 = TestObjectFactory.createCodeAnswerWithExercise(exercise.getId());
        CodeSubmission someOtherSubmission = TestObjectFactory.createCodeAnswerWithExercise(someOtherExercise.getId());

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

        List<CodeSubmission> answers = service.findAllSubmissionsByExerciseAndUserOrderedByVersionDesc(exercise.getId(), userId1);

        Assertions.assertThat(answers.size()).isEqualTo(2);

        Assertions.assertThat(answers.get(1).getId()).isEqualTo(codeSubmission1.getId());
        Assertions.assertThat(answers.get(1).getExerciseId()).isEqualTo(codeSubmission1.getExerciseId());
        Assertions.assertThat(answers.get(1).getVersion()).isEqualTo(codeSubmission1.getVersion());
        Assertions.assertThat(answers.get(1).getVersion()).isEqualTo(0);

        Assertions.assertThat(answers.get(0).getId()).isEqualTo(codeSubmission2.getId());
        Assertions.assertThat(answers.get(0).getExerciseId()).isEqualTo(codeSubmission2.getExerciseId());
        Assertions.assertThat(answers.get(0).getVersion()).isEqualTo(codeSubmission2.getVersion());
        Assertions.assertThat(answers.get(0).getVersion()).isEqualTo(1);

        answers = service.findAllSubmissionsByExerciseAndUserOrderedByVersionDesc(exercise.getId(), userId2);
        Assertions.assertThat(answers.size()).isEqualTo(1);

        Assertions.assertThat(answers.get(0).getId()).isEqualTo(codeSubmission3.getId());
        Assertions.assertThat(answers.get(0).getExerciseId()).isEqualTo(codeSubmission3.getExerciseId());
        Assertions.assertThat(answers.get(0).getVersion()).isEqualTo(codeSubmission3.getVersion());

        answers = service.findAllSubmissionsByExerciseAndUserOrderedByVersionDesc(someOtherExercise.getId(), userId2);
        Assertions.assertThat(answers.size()).isEqualTo(1);

        Assertions.assertThat(answers.get(0).getId()).isEqualTo(someOtherSubmission.getId());
        Assertions.assertThat(answers.get(0).getExerciseId()).isEqualTo(someOtherSubmission.getExerciseId());
        Assertions.assertThat(answers.get(0).getVersion()).isEqualTo(someOtherSubmission.getVersion());
    }

    @Test
    public void findLatestExerciseSubmission() {
        Exercise exercise = TestObjectFactory.createCodeExercise("Hello, world?");
        Exercise someOtherExercise = TestObjectFactory.createCodeExercise("Some other exercise");

        CodeSubmission codeSubmission1 = TestObjectFactory.createCodeAnswerWithExercise(exercise.getId());

        CodeSubmission codeSubmission2 = TestObjectFactory.createCodeAnswerWithExercise(exercise.getId());
        CodeSubmission someOtherSubmission = TestObjectFactory.createCodeAnswerWithExercise(someOtherExercise.getId());

        // Explicitly set user ids for test
        final String userId = "userId-1";
        codeSubmission1.setUserId(userId);
        codeSubmission2.setUserId(userId);
        someOtherSubmission.setUserId(userId);

        service.saveSubmission(codeSubmission1);
        codeSubmission2 = service.saveSubmission(codeSubmission2);
        service.saveSubmission(someOtherSubmission);

        Optional<StudentSubmission> latestSubmissionOptional = service.findLatestExerciseSubmission(exercise.getId(), userId);
        StudentSubmission latestSubmission = latestSubmissionOptional.orElseGet(() -> Assertions.fail("There should be 2 submissions"));

        Assertions.assertThat(latestSubmission.getId()).isEqualTo(codeSubmission2.getId());
    }

    @Test
    public void findLatestExerciseSubmissionNoSubmissionsYet() {
        Exercise exercise = TestObjectFactory.createCodeExercise("Hello, world?");
        final String userId = "userId-1";

        Optional<StudentSubmission> latestSubmission = service.findLatestExerciseSubmission(exercise.getId(), userId);

        Assertions.assertThat(latestSubmission.isPresent()).isFalse();
    }

    @Test
    public void findLatestSubmissionsByAssignment() {
        Assignment assignment = TestObjectFactory.createAssignment("Assignment title");
        Exercise exercise1 = TestObjectFactory.createCodeExercise("Exercise 1");
        Exercise exercise2 = TestObjectFactory.createTextExercise("Exercise 2");
        Exercise exercise3 = TestObjectFactory.createMultipleChoiceExercise("Exercise 3");

        assignment.addExercise(exercise1);
        assignment.addExercise(exercise2);
        assignment.addExercise(exercise3);

        CodeSubmission answer1 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        CodeSubmission codeSubmission2 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        CodeSubmission codeSubmission3 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        TextSubmission answer2 = TestObjectFactory.createTextAnswerWithExercise(exercise2.getId());
        MultipleChoiceSubmission answer3 = TestObjectFactory.createMultipleChoiceAnswerWithExercise(exercise3.getId());

        // Explicitly set user ids for test
        final String userId = "userId-1";
        answer1.setUserId(userId);
        answer2.setUserId(userId);
        answer3.setUserId(userId);
        codeSubmission2.setUserId(userId);
        codeSubmission3.setUserId(userId);

        service.saveSubmission(answer1);
        answer2 = service.saveSubmission(answer2);
        answer3 = service.saveSubmission(answer3);
        service.saveSubmission(codeSubmission2);
        codeSubmission3 = service.saveSubmission(codeSubmission3);

        List<StudentSubmission> latestSubmissionsByAssignment = service.findLatestSubmissionsByAssignment(assignment, userId);
        List<String> ids = latestSubmissionsByAssignment.stream().map(StudentSubmission::getId).collect(Collectors.toList());

        Assertions.assertThat(Set.copyOf(ids)).isEqualTo(Set.of(codeSubmission3.getId(), answer2.getId(), answer3.getId()));
    }
}