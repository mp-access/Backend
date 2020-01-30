package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.dto.UserMigrationResult;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@DataMongoTest
@ActiveProfiles("testing")
@RunWith(SpringRunner.class)
public class CustomizedStudentSubmissionRepositoryImplTest {

    @Autowired
    @Qualifier("customizedStudentSubmissionRepositoryImpl")
    private CustomizedStudentSubmissionRepository repository;

    @Autowired
    private StudentSubmissionRepository submissionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        submissionRepository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        submissionRepository.deleteAll();
    }

    @Test
    public void findByExerciseIdInAndUserIdOrderByVersionDesc() {
        Exercise exercise1 = TestObjectFactory.createCodeExercise("Exercise 1");
        Exercise exercise2 = TestObjectFactory.createTextExercise("Exercise 2");
        Exercise exercise3 = TestObjectFactory.createMultipleChoiceExercise("Exercise 3");

        // Submit multiple versions of exercise 1
        CodeSubmission codeSubmission1 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        CodeSubmission codeSubmission2 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        CodeSubmission codeSubmission3 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        CodeSubmission codeSubmission4 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());

        // Submit once for exercise 2
        TextSubmission answer2 = TestObjectFactory.createTextAnswerWithExercise(exercise2.getId());

        // Submit once for exercise 3
        MultipleChoiceSubmission answer3 = TestObjectFactory.createMultipleChoiceAnswerWithExercise(exercise3.getId());

        // Explicitly set user ids for test
        final String userId = "userId-1";
        codeSubmission1.setUserId(userId);
        codeSubmission1.setGraded(true);
        answer2.setUserId(userId);
        answer2.setGraded(true);
        answer3.setUserId(userId);
        answer3.setGraded(true);
        codeSubmission2.setUserId(userId);
        codeSubmission2.setGraded(true);
        codeSubmission3.setUserId(userId);
        codeSubmission3.setGraded(true);
        codeSubmission4.setUserId(userId);
        codeSubmission4.setGraded(false);

        mongoTemplate.save(codeSubmission1);
        answer2 = mongoTemplate.save(answer2);
        answer3 = mongoTemplate.save(answer3);
        codeSubmission2.setVersion(codeSubmission1.getVersion() + 1);
        codeSubmission2 = mongoTemplate.save(codeSubmission2);
        codeSubmission3.setVersion(codeSubmission2.getVersion() + 1);
        codeSubmission3 = mongoTemplate.save(codeSubmission3);
        codeSubmission4.setVersion(codeSubmission3.getVersion() + 1);
        codeSubmission4 = mongoTemplate.save(codeSubmission4);


        List<StudentSubmission> latestSubmissionsByAssignment = repository
                .findByExerciseIdInAndUserIdAndIsGradedOrderByVersionDesc(
                        List.of(exercise1.getId(), exercise2.getId(), exercise3.getId()),
                        userId);
        List<String> ids = latestSubmissionsByAssignment.stream().map(StudentSubmission::getId).collect(Collectors.toList());
        Set<String> latestSubmissionsForAssignment = Set.of(codeSubmission3.getId(), answer2.getId(), answer3.getId());

        Assertions.assertThat(Set.copyOf(ids))
                .withFailMessage("Submissions should include one submission for each exercise and should include following versions: " + latestSubmissionsByAssignment.toString())
                .isEqualTo(latestSubmissionsForAssignment);

        Assertions.assertThat(ids).doesNotContain(codeSubmission4.getId());
    }

    @Test
    public void invalidateSubmissionsByExerciseId() {
        // Submit multiple versions of exercise 1
        final String exerciseId = "exercise1";
        final String otherExerciseId = "exercise2";
        CodeSubmission codeSubmission1 = TestObjectFactory.createCodeAnswerWithExercise(exerciseId);
        CodeSubmission codeSubmission2 = TestObjectFactory.createCodeAnswerWithExercise(exerciseId);
        CodeSubmission codeSubmission3 = TestObjectFactory.createCodeAnswerWithExercise(otherExerciseId);

        Collection<StudentSubmission> submissions = mongoTemplate.insertAll(List.of(codeSubmission1, codeSubmission2, codeSubmission3));
        Assertions.assertThat(submissions).noneMatch(StudentSubmission::isInvalid);

        repository.invalidateSubmissionsByExerciseId(exerciseId);
        List<StudentSubmission> invalidatedSubmissions = mongoTemplate.find(Query.query(Criteria.where("isInvalid").is(true)), StudentSubmission.class);
        List<StudentSubmission> otherSubmissions = mongoTemplate.find(Query.query(Criteria.where("isInvalid").is(false)), StudentSubmission.class);
        Assertions.assertThat(invalidatedSubmissions).allMatch(StudentSubmission::isInvalid);
        Assertions.assertThat(otherSubmissions).noneMatch(StudentSubmission::isInvalid);
    }

    @Test
    public void migrateUserSubmissions() {
        submissionRepository.deleteAll();

        // Submit multiple versions of exercise 1
        final String exerciseId = "exercise1";
        final String otherExerciseId = "exercise2";
        final String idBefore = "idBefore";
        final String idAfter = "idAfter";
        final String otherId = "other";
        // First submission came with the first user
        StudentSubmission codeSubmission1 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(exerciseId, idBefore, 0));
        StudentSubmission codeSubmission2 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(exerciseId, idBefore, 1));
        StudentSubmission codeSubmission3 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(exerciseId, idBefore, 2));

        // Then the user used the new account to submit more for the same exercise
        StudentSubmission newCodeSubmission1 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(exerciseId, idAfter, 0));
        StudentSubmission newCodeSubmission2 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(exerciseId, idAfter, 1));
        StudentSubmission newCodeSubmission3 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(exerciseId, idAfter, 2));


        StudentSubmission otherSubmission0 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(otherExerciseId, idBefore, 0));
        StudentSubmission otherSubmission1 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(otherExerciseId, idAfter, 0));
        StudentSubmission otherSubmission2 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(otherExerciseId, idAfter, 1));
        StudentSubmission otherSubmission3 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(otherExerciseId, idAfter, 2));

        // Other user's submissions are unaffected
        StudentSubmission otherUserSubmission1 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(otherExerciseId, otherId, 0));
        StudentSubmission otherUserSubmission2 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(otherExerciseId, otherId, 1));
        StudentSubmission otherUserSubmission3 = submissionRepository.save(TestObjectFactory.createCodeAnswerWithExerciseAndUserAndVersion(otherExerciseId, otherId, 2));

        UserMigrationResult submissionsChanged = repository.migrateUserSubmissions(idBefore, idAfter);

        codeSubmission1 = submissionRepository.findById(codeSubmission1.getId()).orElse(null);
        codeSubmission2 = submissionRepository.findById(codeSubmission2.getId()).orElse(null);
        codeSubmission3 = submissionRepository.findById(codeSubmission3.getId()).orElse(null);
        otherSubmission0 = submissionRepository.findById(otherSubmission0.getId()).orElse(null);

        List<StudentSubmission> allByAfter = submissionRepository.findAllByUserId(idAfter);
        Assertions.assertThat(allByAfter.size()).isEqualTo(10);

        Assertions.assertThat(submissionsChanged.isSuccess()).isTrue();
        Assertions.assertThat(submissionsChanged.getNumberOfSubmissionsToMigrate()).isEqualTo(4);
        Assertions.assertThat(submissionsChanged.getNumberOfSubmissionsMigrated()).isEqualTo(4);
        Assertions.assertThat(codeSubmission1).isNotNull();
        Assertions.assertThat(codeSubmission1.getUserId()).isEqualTo(idAfter);
        Assertions.assertThat(codeSubmission1.getVersion()).isEqualTo(-3);
        Assertions.assertThat(codeSubmission2).isNotNull();
        Assertions.assertThat(codeSubmission2.getUserId()).isEqualTo(idAfter);
        Assertions.assertThat(codeSubmission2.getVersion()).isEqualTo(-2);
        Assertions.assertThat(codeSubmission3).isNotNull();
        Assertions.assertThat(codeSubmission3.getUserId()).isEqualTo(idAfter);
        Assertions.assertThat(codeSubmission3.getVersion()).isEqualTo(-1);
        Assertions.assertThat(otherSubmission0).isNotNull();
        Assertions.assertThat(otherSubmission0.getUserId()).isEqualTo(idAfter);
        Assertions.assertThat(otherSubmission0.getVersion()).isEqualTo(-1);

        // Other user's submissions are unaffected
        List<StudentSubmission> otherUsersSubmissions = submissionRepository.findAllByUserId(otherId);
        Assertions.assertThat(otherUsersSubmissions).size().isEqualTo(3);

        int gradedSubmissionCount = submissionRepository.countByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(exerciseId, idAfter);
        Assertions.assertThat(gradedSubmissionCount).isEqualTo(6);

        gradedSubmissionCount = submissionRepository.countByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(otherExerciseId, idAfter);
        Assertions.assertThat(gradedSubmissionCount).isEqualTo(4);
    }
}