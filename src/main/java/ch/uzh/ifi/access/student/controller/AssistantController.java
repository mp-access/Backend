package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.UserMigration;
import ch.uzh.ifi.access.student.dto.UserMigrationResult;
import ch.uzh.ifi.access.student.model.User;
import ch.uzh.ifi.access.student.reporting.AssignmentReport;
import ch.uzh.ifi.access.student.service.AdminSubmissionService;
import ch.uzh.ifi.access.student.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admins")
public class AssistantController {

    private final AdminSubmissionService adminSubmissionService;

    private final CourseService courseService;

    public AssistantController(AdminSubmissionService adminSubmissionService, CourseService courseService) {
        this.adminSubmissionService = adminSubmissionService;
        this.courseService = courseService;
    }

    @GetMapping("/courses/{courseId}/assignments/{assignmentId}/results")
    @PreAuthorize("hasRole(#courseId + '-course-admin')")
    public ResponseEntity<?> exportAssignmentResults(@PathVariable String courseId, @PathVariable String assignmentId) {
        Course course = courseService.getCourseWithPermission(courseId);
        Assignment assignment = course.getAssignmentById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found"));

        AssignmentReport report = adminSubmissionService.generateAssignmentReport(course, assignment);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/courses/{courseId}/assignments/{assignmentId}/reevaluate")
    @PreAuthorize("hasRole(#courseId + '-course-admin')")
    public ResponseEntity<?> reevaluateInvalidateSubmissionsForAssignment(@PathVariable String courseId, @PathVariable String assignmentId) {
        Course course = courseService.getCourseWithPermission(courseId);
        Assignment assignment = course.getAssignmentById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found"));

        adminSubmissionService.reevaluateAssignmentsInvalidSubmissions(course, assignment);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/courses/{courseId}/participants")
    @PreAuthorize("hasRole(#courseId + '-assistant')")
    public ResponseEntity<?> getCourseParticipants(@PathVariable String courseId) {
        Course course = courseService.getCourseWithPermission(courseId);

        UserService.UserQueryResult courseStudents = adminSubmissionService.getCourseStudents(course);
        return ResponseEntity.ok(courseStudents);
    }

    @PostMapping("/courses/{courseId}/participants/migrations")
    @PreAuthorize("hasRole(#courseId + '-course-admin')")
    public ResponseEntity<UserMigrationResult> migrateUser(@RequestBody UserMigration migration, @PathVariable String courseId) {
        log.info("User account migration request: from '{}', to '{}'", migration.getFrom(), migration.getTo());
        Course course = courseService.getCourseWithPermission(courseId);

        UserService.UserQueryResult queryResult = adminSubmissionService.getCourseStudentByUserIds(List.of(migration.getFrom(), migration.getTo()));
        if (!queryResult.getAccountsNotFound().isEmpty()) {
            log.warn("Failed to find accounts to migrate for request {}. Missing accounts {}", migration, queryResult.getAccountsNotFound());
            return ResponseEntity.badRequest().build();
        }

        List<User> users = queryResult.getUsersFound();
        boolean areAllUsersEnrolledInCourse = users.stream().allMatch(user -> course.hasParticipant(user.getEmailAddress()));

        if (!areAllUsersEnrolledInCourse) {
            log.warn("Cannot migrate account. Student is not enrolled in course {}. ", course.getTitle());
            return ResponseEntity.badRequest().build();
        }

        UserMigrationResult migrationResult = adminSubmissionService.migrateUser(migration);
        return ResponseEntity.ok(migrationResult);
    }

    @GetMapping("/courses/{courseId}/exercises/{exerciseId}/users/{userId}/reset")
    @PreAuthorize("hasRole(#courseId + '-assistant')")
    public ResponseEntity<?> resetSubmissionCount(@PathVariable String courseId, @PathVariable String exerciseId, @PathVariable String userId) {
        adminSubmissionService.invalidateSubmissionsByExerciseAndUser(exerciseId, userId);
        return ResponseEntity.ok().build();
    }
}