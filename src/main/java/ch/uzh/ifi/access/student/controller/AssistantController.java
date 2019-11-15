package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.UserMigration;
import ch.uzh.ifi.access.student.dto.UserMigrationResult;
import ch.uzh.ifi.access.student.reporting.AssignmentReport;
import ch.uzh.ifi.access.student.service.AdminSubmissionService;
import ch.uzh.ifi.access.student.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> exportAssignmentResults(@PathVariable String assignmentId, @PathVariable String courseId) {
        Course course = courseService
                .getCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));

        Assignment assignment = course.getAssignmentById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found"));

        AssignmentReport report = adminSubmissionService.generateAssignmentReport(course, assignment);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/courses/{courseId}/assignments/{assignmentId}/reevaluate")
    public ResponseEntity<?> reevaluateInvalidateSubmissionsForAssignment(@PathVariable String assignmentId, @PathVariable String courseId) {
        Course course = courseService
                .getCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));

        Assignment assignment = course.getAssignmentById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found"));

        adminSubmissionService.reevaluateAssignmentsInvalidSubmissions(course, assignment);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/courses/{courseId}/participants")
    public ResponseEntity<?> getCourseParticipants(@PathVariable String courseId) {
        Course course = courseService
                .getCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));

        UserService.UserQueryResult courseStudents = adminSubmissionService.getCourseStudents(course);

        return ResponseEntity.ok(courseStudents);
    }

    @PostMapping("/courses/{courseId}/participants/migrations")
//    @PreAuthorize("@coursePermissionEvaluator.hasAdminAccessToCourse(authentication, #courseId)")
    public ResponseEntity<UserMigrationResult> migrateUser(@RequestBody UserMigration migration, @PathVariable String courseId) {
        log.info("User account migration request: from '{}', to '{}'", migration.getFrom(), migration.getTo());
        UserMigrationResult migrationResult = adminSubmissionService.migrateUser(migration);
        return ResponseEntity.ok(migrationResult);
    }
}
