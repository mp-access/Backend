package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.AssignmentResults;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/students")
public class ResultController {

    private CourseService courseService;
    private StudentSubmissionService submissionService;

    @Autowired
    public ResultController(CourseService courseService, StudentSubmissionService submissionService) {
        this.courseService = courseService;
        this.submissionService = submissionService;
    }

    @GetMapping("/courses/{courseId}/results")
    public ResponseEntity<List> getCourseResults(@PathVariable String courseId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");
        String userId = authentication.getUserId();

        Course course = courseService
                .getCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));

        List<AssignmentResults> courseResults = new ArrayList<>();

        List<Assignment> assignments = course.getAssignments();
        for (Assignment a : assignments) {
            courseResults.add(AssignmentResults.builder()
                    .assignmentId(a.getId())
                    .userId(userId)
                    .maxScore(a.getMaxScore())
                    .gradedSubmissions(submissionService.findLatestSubmissionsByAssignment(a, userId))
                    .build());
        }

        return ResponseEntity.ok(courseResults);
    }

}
