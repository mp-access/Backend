package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.dto.CourseMetadataDTO;
import ch.uzh.ifi.access.course.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Get all cached courses, filter by course role name and convert to metadata. Only courses with a
     * role name that appears in the role name list of the user who made the request are returned.
     * @return        list of CourseMetadataDTO representing all cached courses the user can access
     */
    @GetMapping
    @PostFilter("hasRole(filterObject.roleName)")
    public List<CourseMetadataDTO> getEnrolledCourses() {
        logger.info("Fetching all enrolled courses");
        return courseService.getAllCourses().stream().map(CourseMetadataDTO::new).collect(Collectors.toList());
    }

    /**
     * Get a course by its role name and convert it to metadata if the user has the requested course role name.
     * @param course  requested course role name
     * @return        CourseMetadataDTO of the requested course, if accessible and found
     * @see CourseService#getCourseWithPermission(String)  for permission filtering and exceptions
     */
    @GetMapping(path = "/{course}")
    public CourseMetadataDTO getEnrolledCourse(@PathVariable String course) {
        logger.info("Fetching course {}", course);
        return new CourseMetadataDTO(courseService.getCourseWithPermission(course));
    }

    /**
     * Get all assignments if the user has an assistant role, else filter out unpublished assignments.
     * @param course  requested course role name
     * @return        list of AssignmentMetadataDTO representing all assignments the user can access
     * @see CourseService#getCourseWithPermission(String)  for permission filtering and exceptions
     */
    @GetMapping(path = "/{course}/assignments")
    @PostFilter("filterObject.published or hasRole(#course + '-assistant')")
    public List<AssignmentMetadataDTO> getAllAssignmentsByCourse(@PathVariable String course) {
        logger.info("Fetching all assignments for course {}", course);
        return courseService.getCourseWithPermission(course).getAssignments()
                .stream().map(AssignmentMetadataDTO::new).collect(Collectors.toList());
    }

    /**
     * Find a specific assignment in the list of all assignments the user can access.
     * @param course        requested course role name
     * @param assignmentId  requested assignment ID
     * @return              AssignmentMetadataDTO of the requested assignment, if accessible and found
     * @throws ResourceNotFoundException    if the requested assignment is not found
     * @see #getAllAssignmentsByCourse(String)
     */
    @GetMapping("/{course}/assignments/{assignmentId}")
    @PostAuthorize("returnObject.published or hasRole(#course + '-assistant')")
    public AssignmentMetadataDTO getCourseAssignment(@PathVariable String course, @PathVariable String assignmentId) {
        logger.info("Fetching assignment ID {} for course {}", assignmentId, course);
        return getAllAssignmentsByCourse(course)
                .stream().filter(assignment -> assignment.getId().equals(assignmentId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found"));
    }
}