package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.dto.AssignmentDTO;
import ch.uzh.ifi.access.course.dto.CourseDTO;
import ch.uzh.ifi.access.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<CourseDTO> getAllCourses() {
        List<CourseDTO> courses = new ArrayList<>();
        for (Course c : courseService.getAllCourses()) {
            courses.add(new CourseDTO(c));
        }
        return courses;
    }

    @GetMapping(path = "{id}")
    public CourseDTO getCourseById(@PathVariable("id") UUID id) {
        return new CourseDTO((courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("No course found"))));
    }

    @GetMapping(path = "{id}/assignments")
    public List<AssignmentDTO> getAllAssignmentsByCourseId(@PathVariable("id") UUID id) {
        CourseDTO cd = new CourseDTO((courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("No course found"))));
        return cd.getAssignments();
    }


    @GetMapping("/{courseId}/assignments/{assignmentId}")
    public AssignmentDTO getAssignmentByCourseId(@PathVariable("courseId") UUID courseId, @PathVariable("assignmentId") UUID assignmentId) {
        return new AssignmentDTO(courseService.getCourseById(courseId)
                .map(course -> course.getAssignmentById(assignmentId))
                .map(a -> a.orElseThrow(() -> new IllegalArgumentException("No assignment found")))
                .orElseThrow(() -> new IllegalArgumentException("No course found")));
    }



}
