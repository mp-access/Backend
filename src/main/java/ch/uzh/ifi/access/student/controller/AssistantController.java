package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.reporting.AssignmentReport;
import ch.uzh.ifi.access.student.service.AdminSubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/courses/{courseId}/assignments/{assignmentId}/evaluation")
    public ResponseEntity<?> triggerEvaluationOfAssignment(@PathVariable String assignmentId, @PathVariable String courseId) {
        Course course = courseService
                .getCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));

        Assignment assignment = course.getAssignmentById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found"));

        adminSubmissionService.triggerReEvaluation(course, assignment);
        return null;
    }
}
