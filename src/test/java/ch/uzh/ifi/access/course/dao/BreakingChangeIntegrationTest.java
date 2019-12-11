package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BreakingChangeIntegrationTest {

    @Autowired
    private CourseDAO courseDAO;

    @Autowired
    private StudentSubmissionService submissionService;

    @Autowired
    private StudentSubmissionRepository repository;

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void lookForBreakingChangesSingleExerciseBroken() throws InterruptedException {
        // Set up
        Course before = TestObjectFactory.createCourse("title");
        Course after = TestObjectFactory.createCourse(before.getTitle());
        Assignment assignmentBefore = TestObjectFactory.createAssignment("assignment");
        Assignment assignmentAfter = TestObjectFactory.createAssignment("assignment");
        Exercise exerciseBefore1 = TestObjectFactory.createCodeExercise("");
        Exercise exerciseAfter1 = TestObjectFactory.createCodeExercise("");
        exerciseBefore1.setOrder(0);
        exerciseAfter1.setOrder(1);

        Exercise exerciseBefore2 = TestObjectFactory.createCodeExercise("");
        Exercise exerciseAfter2 = TestObjectFactory.createCodeExercise("");
        exerciseBefore2.setOrder(2);
        exerciseAfter2.setOrder(exerciseBefore2.getOrder());

        before.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exerciseBefore1);
        assignmentBefore.addExercise(exerciseBefore2);

        after.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exerciseAfter1);
        assignmentAfter.addExercise(exerciseAfter2);

        final String userId = "user-id1";
        CodeSubmission submission1 = new CodeSubmission();
        submission1.setUserId(userId);
        submission1.setExerciseId(exerciseBefore1.getId());
        submission1.setCommitId(exerciseBefore1.getGitHash());
        submission1.setGraded(true);

        CodeSubmission submission2 = new CodeSubmission();
        submission2.setUserId(userId);
        submission2.setExerciseId(exerciseBefore2.getId());
        submission2.setCommitId(exerciseBefore2.getGitHash());
        submission2.setGraded(true);

        // Begin actual test
        // Submit answers for each exercise
        submissionService.initSubmission(submission1);
        submissionService.initSubmission(submission2);

        // Make sure they are both valid
        List<StudentSubmission> all = repository.findAll();
        Assertions.assertThat(all).allMatch(studentSubmission -> !studentSubmission.isInvalid());

        // Look for breaking changes and invalidate submission for exercise1 which contains breaking change
        List<Exercise> breakingChanges = courseDAO.lookForBreakingChanges(before, after);
        courseDAO.updateCourse(before, after);

        // Wait 2 seconds to wait for asynchronous execution to finish invalidating answers
        Thread.sleep(2000);

        // Exercise 1 submission should be invalidated, exercise 2 should still be valid
        List<StudentSubmission> exercise1Submissions = repository.findByExerciseIdInAndUserIdAndIsGradedOrderByVersionDesc(List.of(exerciseBefore1.getId()), userId);
        List<StudentSubmission> exercise2Submissions = repository.findByExerciseIdInAndUserIdAndIsGradedOrderByVersionDesc(List.of(exerciseBefore2.getId()), userId);

        Assertions.assertThat(exercise1Submissions).size().isEqualTo(1);
        Assertions.assertThat(exercise2Submissions).size().isEqualTo(1);

        StudentSubmission invalidSubmission = exercise1Submissions.get(0);
        Assertions.assertThat(invalidSubmission.isInvalid()).isTrue();

        StudentSubmission nonUpdatedExercise = exercise2Submissions.get(0);
        Assertions.assertThat(nonUpdatedExercise.isInvalid()).isFalse();
    }
}
