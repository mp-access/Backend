package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.student.dto.UserMigration;
import ch.uzh.ifi.access.student.dto.UserMigrationResult;
import ch.uzh.ifi.access.student.evaluation.EvalProcessService;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.User;
import ch.uzh.ifi.access.student.reporting.AssignmentReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminSubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(AdminSubmissionService.class);

    private final StudentSubmissionService submissionService;

    private final UserService userService;

    private final EvalProcessService evaluationService;

    public AdminSubmissionService(StudentSubmissionService submissionService, UserService userService, EvalProcessService evaluationService) {
        this.submissionService = submissionService;
        this.userService = userService;
        this.evaluationService = evaluationService;
    }

    @PreAuthorize("@coursePermissionEvaluator.hasAdminAccessToCourse(authentication, #course)")
    public AssignmentReport generateAssignmentReport(Course course, Assignment assignment) {
        UserService.UserQueryResult students = userService.getCourseStudents(course);

        Map<User, List<StudentSubmission>> submissionsByStudent = new HashMap<>();
        for (User student : students.getUsersFound()) {
            List<StudentSubmission> studentSubmissionsForAssignment = submissionService.findLatestGradedSubmissionsByAssignment(assignment, student.getId());
            submissionsByStudent.put(student, studentSubmissionsForAssignment);
        }

        return new AssignmentReport(assignment, students.getUsersFound(), submissionsByStudent, students.getAccountsNotFound());
    }

    @PreAuthorize("@coursePermissionEvaluator.hasAdminAccessToCourse(authentication, #course)")
    public void reevaluateAssignmentsInvalidSubmissions(Course course, Assignment assignment) {
        Assert.notNull(course, "Course cannot be null");
        Assert.notNull(assignment, "Assignment cannot be null");

        logger.info(String.format("Re-Evaluate invalidated submissions for assignment %s.", assignment.getId()));

        UserService.UserQueryResult students = userService.getCourseStudents(course);
        Map<User, List<StudentSubmission>> invalidatedSubsByStudent = getInvalidatedSubmissionsForUsers(assignment, students.getUsersFound());
        for (User u : invalidatedSubsByStudent.keySet()) {
            logger.debug(String.format("Re-Evaluate invalidated submissions for user.", u.getId()));
            triggerReEvaluation(invalidatedSubsByStudent.get(u));
        }

        logger.info("Re-Evaluations triggered");
    }

    private Map<User, List<StudentSubmission>> getInvalidatedSubmissionsForUsers(Assignment assignment, List<User> students) {
        Map<User, List<StudentSubmission>> invalidatedSubsByStudent = new HashMap<>();
        for (User student : students) {
            List<StudentSubmission> invalidatedSubmissionsForUser = submissionService.findLatestGradedInvalidatedSubmissionsByAssignment(assignment, student.getId());
            if (invalidatedSubmissionsForUser != null && invalidatedSubmissionsForUser.size() > 0) {
                invalidatedSubsByStudent.put(student, invalidatedSubmissionsForUser);
            }
        }
        logger.debug(String.format("Found %s users with invalidated submission for this assignment", invalidatedSubsByStudent.size()));
        return invalidatedSubsByStudent;
    }

    protected void triggerReEvaluation(List<StudentSubmission> submissions) {

        for (StudentSubmission sub : submissions) {
            logger.debug(String.format("Prepare re-evaluation for submission: %s", sub.getId()));

            StudentSubmission stripped = sub.stripSubmissionForReEvaluation();
            stripped.setTimestamp(Instant.now());
            stripped.setGraded(true);
            stripped.setTriggeredReSubmission(true);

            StudentSubmission reSubmitted = submissionService.initSubmission(stripped);
            String processId = evaluationService.initEvalProcess(reSubmitted);

            logger.debug(String.format("Fire re-evaluation for processId %s with new submission %s)", reSubmitted.getId(), processId));
            evaluationService.fireEvalProcessExecutionAsync(processId);
        }
    }

    @PreAuthorize("@coursePermissionEvaluator.hasPrivilegedAccessToCourse(authentication, #course)")
    public UserService.UserQueryResult getCourseStudents(Course course) {
        return userService.getCourseStudents(course);
    }

    public UserService.UserQueryResult getCourseStudentByUserIds(List<String> userIds, Course course) {
        return userService.getUsersByIds(userIds);
    }

    public UserMigrationResult migrateUser(UserMigration userMigration) {
        return submissionService.migrateUserSubmissions(userMigration.getFrom(), userMigration.getTo());
    }
}
