package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.reporting.AssignmentReport;
import ch.uzh.ifi.access.student.service.AdminSubmissionService;
import ch.uzh.ifi.access.student.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
