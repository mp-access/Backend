package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.config.ApiTokenAuthenticationProvider;
import ch.uzh.ifi.access.course.FilterByPublishingDate;
import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.dto.CourseMetadataDTO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.model.User;
import ch.uzh.ifi.access.student.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @FilterByPublishingDate
    @GetMapping
    public List<CourseMetadataDTO> getAllCourses() {
        List<CourseMetadataDTO> courses = new ArrayList<>();
        for (Course c : courseService.getAllCourses()) {
            courses.add(new CourseMetadataDTO(c));
        }
        return courses;
    }

    @GetMapping(path = "{id}")
    public CourseMetadataDTO getCourseById(@PathVariable("id") String id) {
        return new CourseMetadataDTO(courseService
                .getCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No course found")));
    }

    @GetMapping(path = "{id}/assignments")
    public List<AssignmentMetadataDTO> getAllAssignmentsByCourseId(@PathVariable("id") String id) {
        CourseMetadataDTO cd = getCourseById(id);
        return cd.getAssignments();
    }

    @FilterByPublishingDate
    @GetMapping("/{courseId}/assignments/{assignmentId}")
    public ResponseEntity<AssignmentMetadataDTO> getAssignmentByCourseId(@PathVariable("courseId") String courseId, @PathVariable("assignmentId") String assignmentId, CourseAuthentication authentication) {
        AssignmentMetadataDTO assignment = courseService.getCourseById(courseId)
                .flatMap(course -> course.getAssignmentById(assignmentId))
                .map(AssignmentMetadataDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found"));

        if (assignment.isPublished() || authentication.hasAdminAccess(courseId)) {
            return ResponseEntity.ok(assignment);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{courseId}/assistants")
    public ResponseEntity<?> getCourseAssistants(@PathVariable String courseId) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));

        List<User> users = userService.getCourseAdmins(course);
        return ResponseEntity.ok(users);
    }

    @PostMapping(path = "{id}/update")
    public void updateCourse(@PathVariable("id") String id, @RequestBody String json,
                             ApiTokenAuthenticationProvider.GithubHeaderAuthentication authentication) {
        logger.debug("Received web hook");

        if (!authentication.matchesHmacSignature(json)) {
            throw new BadCredentialsException("Hmac signature does not match!");
        }

        logger.debug("Updating courses");
        courseService.updateCourseById(id);
    }

}
