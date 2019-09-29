package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.User;
import ch.uzh.ifi.access.student.reporting.AssignmentReport;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminSubmissionService {

    private final StudentSubmissionService submissionService;

    private final UserService userService;

    public AdminSubmissionService(StudentSubmissionService submissionService, UserService userService) {
        this.submissionService = submissionService;
        this.userService = userService;
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
}
