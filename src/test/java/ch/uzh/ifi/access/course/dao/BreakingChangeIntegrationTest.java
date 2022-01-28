package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class BreakingChangeIntegrationTest {

    @Autowired
    private CourseDAO courseDAO;

    @Autowired
    private StudentSubmissionService submissionService;

    @Test
    public void lookForBreakingChangesSingleExerciseBroken() throws InterruptedException {
        // Set up
        Course courseBefore = new Course("Course");
        Assignment assignmentBefore = new Assignment("Assignment");
        Exercise exercise1Before = TestObjectFactory.createCodeExercise("exercise-1");
        Exercise exercise2Before = TestObjectFactory.createCodeExercise("exercise-2");
        exercise1Before.setOrder(0);
        exercise2Before.setOrder(2);
        courseBefore.addAssignment(assignmentBefore);
        assignmentBefore.addExercise(exercise1Before);
        assignmentBefore.addExercise(exercise2Before);

        Course courseAfter = new Course("Course");
        Assignment assignmentAfter = new Assignment("Assignment");
        Exercise exercise1After = TestObjectFactory.createCodeExercise("exercise-1");
        Exercise exercise2After = TestObjectFactory.createCodeExercise("exercise-2");
        exercise1After.setOrder(1);
        exercise2After.setOrder(2);
        courseAfter.addAssignment(assignmentAfter);
        assignmentAfter.addExercise(exercise1After);
        assignmentAfter.addExercise(exercise2After);

        final String userId = "test-user";
        CodeSubmission submissionEx1 = new CodeSubmission();
        submissionEx1.setUserId(userId);
        submissionEx1.setExerciseId(exercise1Before.getId());
        submissionEx1.setCommitId(exercise1Before.getGitHash());
        submissionEx1.setGraded(true);

        CodeSubmission submissionEx2 = new CodeSubmission();
        submissionEx2.setUserId(userId);
        submissionEx2.setExerciseId(exercise2Before.getId());
        submissionEx2.setCommitId(exercise2Before.getGitHash());
        submissionEx2.setGraded(true);

        // Begin actual test
        // Submit answers for each exercise
        submissionService.initSubmission(submissionEx1);
        submissionService.initSubmission(submissionEx2);

        // Make sure they are both valid
        submissionService.findAll().forEach(studentSubmission -> Assertions.assertFalse(studentSubmission.isInvalid()));

        // Look for breaking changes and invalidate submission for exercise1 which contains breaking change
        courseDAO.lookForBreakingChanges(courseBefore, courseAfter);
        courseDAO.updateCourse(courseBefore, courseAfter);

        // Wait 2 seconds to wait for asynchronous execution to finish invalidating answers
        Thread.sleep(2000);

        // Exercise 1 submission should be invalidated, exercise 2 should still be valid
        Optional<StudentSubmission> returnedSubmissionEx1 = submissionService.findLatestExerciseSubmission(exercise1Before.getId(), userId);
        Assertions.assertTrue(returnedSubmissionEx1.isPresent());
        Assertions.assertTrue(returnedSubmissionEx1.get().isInvalid());

        Optional<StudentSubmission> returnedSubmissionEx2 = submissionService.findLatestExerciseSubmission(exercise2Before.getId(), userId);
        Assertions.assertTrue(returnedSubmissionEx2.isPresent());
        Assertions.assertFalse(returnedSubmissionEx2.get().isInvalid());
    }
}
