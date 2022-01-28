package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.AssignmentResults;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ResultController {

    private CourseService courseService;
    private StudentSubmissionService submissionService;

    @Autowired
    public ResultController(CourseService courseService, StudentSubmissionService submissionService) {
        this.courseService = courseService;
        this.submissionService = submissionService;
    }

    @GetMapping("/courses/{course}/results")
    public List<AssignmentResults> getCourseResults(@PathVariable String course, Principal principal) {
        return courseService.getCourseByRoleName(course)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"))
                .getAssignments().stream().map(assignment -> AssignmentResults.builder()
                    .assignmentId(assignment.getId())
                    .userId(principal.getName())
                    .maxScore(assignment.getMaxScore())
                    .gradedSubmissions(submissionService.findLatestGradedSubmissionsByAssignment(assignment, principal.getName()))
                .build()).collect(Collectors.toList());
    }
}