package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.student.dto.UserMigrationResult;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@DataMongoTest
public class CustomizedStudentSubmissionRepositoryImplTest {

    final String exerciseId1 = "test-exercise-1";
    final String exerciseId2 = "test-exercise-2";

    @Autowired
    private StudentSubmissionRepository submissionRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        submissionRepository.deleteAll();
    }

    @Test
    public void invalidateSubmissionsByExerciseId() {
        // Submit multiple versions of exercise 1
        final String userId = "test-user";
        CodeSubmission codeSubmission1 = TestObjectFactory.createCodeAnswer("submission-1", userId, exerciseId1, 1);
        CodeSubmission codeSubmission2 = TestObjectFactory.createCodeAnswer("submission-2", userId, exerciseId1, 2);
        CodeSubmission codeSubmission3 = TestObjectFactory.createCodeAnswer("submission-3", userId, exerciseId2, 1);

        Collection<StudentSubmission> submissions = mongoTemplate.insertAll(List.of(codeSubmission1, codeSubmission2, codeSubmission3));
        submissions.forEach(submission -> Assertions.assertFalse(submission.isInvalid()));

        submissionRepository.invalidateSubmissionsByExerciseId(exerciseId1);
        List<StudentSubmission> invalidatedSubmissions = mongoTemplate.find(Query.query(Criteria.where("isInvalid").is(true)), StudentSubmission.class);
        List<StudentSubmission> otherSubmissions = mongoTemplate.find(Query.query(Criteria.where("isInvalid").is(false)), StudentSubmission.class);
        invalidatedSubmissions.forEach(submission -> Assertions.assertTrue(submission.isInvalid()));
        otherSubmissions.forEach(submission -> Assertions.assertFalse(submission.isInvalid()));
    }

    @Test
    public void migrateUserSubmissions() {
        final String oldAccount = "test-old-account";
        final String newAccount = "test-new-account";
        final String otherUser = "other-user";

        // The user submitted solutions to exercise 1 using their old account
        StudentSubmission oldAccountSubmission1 = submissionRepository.save(
                TestObjectFactory.createCodeAnswer(oldAccount, exerciseId1, 0));
        StudentSubmission oldAccountSubmission2 = submissionRepository.save(
                TestObjectFactory.createCodeAnswer(oldAccount, exerciseId1, 1));
        StudentSubmission oldAccountSubmission3 = submissionRepository.save(
                TestObjectFactory.createCodeAnswer(oldAccount, exerciseId1, 2));

        // The user submitted additional solutions to exercise 1 using their new account
        submissionRepository.save(TestObjectFactory.createCodeAnswer(newAccount, exerciseId1, 0));
        submissionRepository.save(TestObjectFactory.createCodeAnswer(newAccount, exerciseId1, 1));
        submissionRepository.save(TestObjectFactory.createCodeAnswer(newAccount, exerciseId1, 2));

        // The user submitted solutions to exercise 2 using both accounts
        StudentSubmission oldAccountSubmission4 = submissionRepository.save(
                TestObjectFactory.createCodeAnswer(oldAccount, exerciseId2, 0));
        submissionRepository.save(TestObjectFactory.createCodeAnswer(newAccount, exerciseId2, 0));
        submissionRepository.save(TestObjectFactory.createCodeAnswer(newAccount, exerciseId2, 1));
        submissionRepository.save(TestObjectFactory.createCodeAnswer(newAccount, exerciseId2, 2));

        // Submissions by other users
        submissionRepository.save(TestObjectFactory.createCodeAnswer(otherUser, exerciseId2, 0));
        submissionRepository.save(TestObjectFactory.createCodeAnswer(otherUser, exerciseId2, 1));
        submissionRepository.save(TestObjectFactory.createCodeAnswer(otherUser, exerciseId2, 2));

        UserMigrationResult submissionsChanged = submissionRepository.migrateUserSubmissions(oldAccount, newAccount);
        Assertions.assertTrue(submissionsChanged.isSuccess());
        Assertions.assertEquals(4, submissionsChanged.getNumberOfSubmissionsToMigrate());
        Assertions.assertEquals(4, submissionsChanged.getNumberOfSubmissionsMigrated());

        Optional<StudentSubmission> migratedSubmission1 = submissionRepository.findById(oldAccountSubmission1.getId());
        Assertions.assertTrue(migratedSubmission1.isPresent());
        Assertions.assertEquals(newAccount, migratedSubmission1.get().getUserId());
        Assertions.assertEquals(-3, migratedSubmission1.get().getVersion());

        Optional<StudentSubmission> migratedSubmission2 = submissionRepository.findById(oldAccountSubmission2.getId());
        Assertions.assertTrue(migratedSubmission2.isPresent());
        Assertions.assertEquals(newAccount, migratedSubmission2.get().getUserId());
        Assertions.assertEquals(-2, migratedSubmission2.get().getVersion());

        Optional<StudentSubmission> migratedSubmission3 = submissionRepository.findById(oldAccountSubmission3.getId());
        Assertions.assertTrue(migratedSubmission3.isPresent());
        Assertions.assertEquals(newAccount, migratedSubmission3.get().getUserId());
        Assertions.assertEquals(-1, migratedSubmission3.get().getVersion());

        Optional<StudentSubmission> migratedSubmission4 = submissionRepository.findById(oldAccountSubmission4.getId());
        Assertions.assertTrue(migratedSubmission4.isPresent());
        Assertions.assertEquals(newAccount, migratedSubmission4.get().getUserId());
        Assertions.assertEquals(-1, migratedSubmission4.get().getVersion());

        // Submissions by other users should not be affected
        Assertions.assertEquals(3, submissionRepository.findAllByUserId(otherUser).size());
    }
}