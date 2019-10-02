package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.config.ApiTokenAuthenticationProvider;
import ch.uzh.ifi.access.course.CheckCoursePermission;
import ch.uzh.ifi.access.course.FilterByPublishingDate;
import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.dto.CourseMetadataDTO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.course.util.CoursePermissionEnforcer;
import ch.uzh.ifi.access.student.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    private final UserService userService;

    private final CoursePermissionEnforcer permissionEnforcer;

    public CourseController(CourseService courseService, UserService userService, CoursePermissionEnforcer permissionEnforcer) {
        this.courseService = courseService;
        this.userService = userService;
        this.permissionEnforcer = permissionEnforcer;
    }

    @CheckCoursePermission
    @FilterByPublishingDate
    @GetMapping
    public List<CourseMetadataDTO> getAllCourses() {
        List<CourseMetadataDTO> courses = new ArrayList<>();
        for (Course c : courseService.getAllCourses()) {
            courses.add(new CourseMetadataDTO(c));
        }
        return courses;
    }

    @PreAuthorize("@coursePermissionEvaluator.hasAccessToCourse(authentication, #id)")
    @GetMapping(path = "{id}")
    public CourseMetadataDTO getCourseById(@PathVariable("id") String id) {
        return new CourseMetadataDTO(courseService
                .getCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No course found")));
    }

    @PreAuthorize("@coursePermissionEvaluator.hasAccessToCourse(authentication, #id)")
    @GetMapping(path = "{id}/assignments")
    public List<AssignmentMetadataDTO> getAllAssignmentsByCourseId(@PathVariable("id") String id) {
        CourseMetadataDTO cd = getCourseById(id);
        return cd.getAssignments();
    }

    @PreAuthorize("@coursePermissionEvaluator.hasAccessToCourse(authentication, #courseId)")
    @FilterByPublishingDate
    @GetMapping("/{courseId}/assignments/{assignmentId}")
    public ResponseEntity<AssignmentMetadataDTO> getAssignmentByCourseId(@PathVariable("courseId") String courseId, @PathVariable("assignmentId") String assignmentId, @ApiIgnore CourseAuthentication authentication) {
        AssignmentMetadataDTO assignment = courseService.getCourseById(courseId)
                .flatMap(course -> course.getAssignmentById(assignmentId))
                .map(AssignmentMetadataDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found"));

        return permissionEnforcer.shouldAccessAssignment(assignment, courseId, authentication)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{courseId}/assistants")
    public ResponseEntity<?> getCourseAssistants(@PathVariable String courseId) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));

        UserService.UserQueryResult users = userService.getCourseAdmins(course);
        return ResponseEntity.ok(users.getUsersFound());
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
