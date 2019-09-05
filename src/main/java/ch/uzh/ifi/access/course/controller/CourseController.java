package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.config.ApiTokenAuthenticationProvider;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.dto.CourseMetadataDTO;
import ch.uzh.ifi.access.course.dto.ExerciseWithSolutionsDTO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.model.User;
import ch.uzh.ifi.access.student.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public List<CourseMetadataDTO> getAllCourses() {
        List<CourseMetadataDTO> courses = new ArrayList<>();
        for (Course c : courseService.getAllCourses()) {
            courses.add(new CourseMetadataDTO(c));
        }
        return courses;
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

    @GetMapping("/{courseId}/assignments/{assignmentId}")
    public AssignmentMetadataDTO getAssignmentByCourseId(@PathVariable("courseId") String courseId, @PathVariable("assignmentId") String assignmentId) {
        return new AssignmentMetadataDTO(courseService.getCourseById(courseId)
                .flatMap(course -> course.getAssignmentById(assignmentId))
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found")));
    }

    @GetMapping("/{courseId}/assistants")
    public ResponseEntity<?> getCourseAssistants(@PathVariable String courseId) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));

        List<User> users = userService.getCourseAdmins(course);
        return ResponseEntity.ok(users);
    }
}
