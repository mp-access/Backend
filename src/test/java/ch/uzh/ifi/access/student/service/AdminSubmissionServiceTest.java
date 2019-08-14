package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.User;
import ch.uzh.ifi.access.student.reporting.AssignmentReport;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DataMongoTest
@RunWith(SpringRunner.class)
public class AdminSubmissionServiceTest {

    @Autowired
    private StudentSubmissionRepository studentSubmissionRepository;

    private AdminSubmissionService service;

    private StudentSubmissionService submissionService;

    @Mock
    private UserService userService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.submissionService = new StudentSubmissionService(studentSubmissionRepository);
        this.service = new AdminSubmissionService(submissionService, userService);
    }

    @After
    public void tearDown() {
        studentSubmissionRepository.deleteAll();
    }

    /**
     * 0. Create at least 2 students
     * 1. Create assigment
     * 2. Create at least 2 exercises
     * 3. Create at least 2 submissions for each exercise for each student
     * 4. Fetch the latest submission for each student for each exercise in the assignment
     */
    @Test
    public void generateAssignmentReportTest() {
        // 0. Create 2 students
        final String user1Id = "userId-1";
        final String user1Email = "userId-1@example.com";
        final String user2Id = "userId-2";
        final String user2Email = "userId-2@example.com";

        // 1. & 2. Create assignment and 2 exercises
        Assignment assignment = TestObjectFactory.createAssignment("Assignment title");
        Exercise exercise1 = TestObjectFactory.createCodeExercise("Exercise 1");
        Exercise exercise2 = TestObjectFactory.createTextExercise("Exercise 2");
        assignment.addExercise(exercise1);
        assignment.addExercise(exercise2);

        // 3. Submit multiple versions
        // Submit multiple versions of exercise 1
        CodeSubmission ex1Submission1User1 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        CodeSubmission ex1Submission2User1 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        CodeSubmission ex1Submission1User2 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());
        CodeSubmission ex1Submission2User2 = TestObjectFactory.createCodeAnswerWithExercise(exercise1.getId());

        // Submit multiple versions of exercise 2
        CodeSubmission ex2Submission1User1 = TestObjectFactory.createCodeAnswerWithExercise(exercise2.getId());
        CodeSubmission ex2Submission2User1 = TestObjectFactory.createCodeAnswerWithExercise(exercise2.getId());
        CodeSubmission ex2Submission1User2 = TestObjectFactory.createCodeAnswerWithExercise(exercise2.getId());
        CodeSubmission ex2Submission2User2 = TestObjectFactory.createCodeAnswerWithExercise(exercise2.getId());

        setUserForSubmission(List.of(ex1Submission1User1, ex1Submission2User1, ex2Submission1User1, ex2Submission2User1), user1Id);
        setUserForSubmission(List.of(ex1Submission1User2, ex1Submission2User2, ex2Submission1User2, ex2Submission2User2), user2Id);

        final double ex1User1Score = 5.0;
        final SubmissionEvaluation.Points ex1User1Points = new SubmissionEvaluation.Points(5,10);
        final double ex1User2Score = 10.0;
        final SubmissionEvaluation.Points ex1User2Points = new SubmissionEvaluation.Points(10,10);
        setResultForSubmission(ex1Submission2User1, ex1User1Points, exercise1.getMaxScore());
        setResultForSubmission(ex1Submission2User2, ex1User2Points, exercise1.getMaxScore());

        submissionService.initSubmission(ex1Submission1User1);
        submissionService.initSubmission(ex1Submission2User1);

        submissionService.initSubmission(ex1Submission1User2);
        submissionService.initSubmission(ex1Submission2User2);

        submissionService.initSubmission(ex2Submission1User1);
        submissionService.initSubmission(ex2Submission2User1);

        submissionService.initSubmission(ex2Submission1User2);
        submissionService.initSubmission(ex2Submission2User2);

        Course course = new Course("");
        course.addAssignment(assignment);
        final String assignmentId = assignment.getId();

        User user1 = new User(user1Id, user1Email);
        User user2 = new User(user2Id, user2Email);
        List<User> students = List.of(user1, user2);
        course.setStudents(students.stream().map(User::getEmailAddress).collect(Collectors.toList()));

        when(userService.getCourseStudents(eq(course))).thenReturn(students);
        AssignmentReport report = service.generateAssignmentReport(course, assignment);

        Assertions.assertThat(report).isNotNull();
        Assertions.assertThat(report.getAssignmentId()).isEqualTo(assignmentId);
        Map<String, Map<String, SubmissionEvaluation>> submissionByExerciseIdAndUserId = report.getByExercises();

        // assert that there is an entry for each exercise of an assignment
        assignment.getExercises().forEach(exercise -> Assertions.assertThat(submissionByExerciseIdAndUserId).containsKey(exercise.getId()));

        // assert that for each exercise there is an entry for each student, regardless if the submitted or not
        for (String exerciseId : submissionByExerciseIdAndUserId.keySet()) {
            Map<String, SubmissionEvaluation> studentSubmissions = report.getByExercises().get(exerciseId);

            students.forEach(student -> Assertions.assertThat(studentSubmissions).containsKey(student.getEmailAddress()));
        }

        Assertions.assertThat(submissionByExerciseIdAndUserId.get(exercise1.getId()).get(user1.getEmailAddress()).getScore()).isEqualTo(ex1User1Score);
        Assertions.assertThat(submissionByExerciseIdAndUserId.get(exercise1.getId()).get(user2.getEmailAddress()).getScore()).isEqualTo(ex1User2Score);
    }

    private void setUserForSubmission(List<StudentSubmission> submissions, String userId) {
        submissions.forEach(submission -> submission.setUserId(userId));
    }

    private void setResultForSubmission(StudentSubmission submission, SubmissionEvaluation.Points points, int maxScore ) {
        submission.setResult( SubmissionEvaluation.builder().points(points).maxScore( maxScore).timestamp(Instant.now()).build());
    }
}