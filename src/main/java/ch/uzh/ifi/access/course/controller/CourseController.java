package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses(){
        return courseService.getAllCourses();
    }

    @GetMapping(path = "{id}")
    public Course getCourseById(@PathVariable("id") UUID id) {
        return courseService.getCourseById(id)
                .orElse(null);
    }
}
