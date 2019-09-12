package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Exercise;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@DataMongoTest
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
}