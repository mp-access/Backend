package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.dto.CourseDTO;
import ch.uzh.ifi.access.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses(){
        return courseService.getAllCourses();

    }

    @GetMapping(path = "{id}")
    public CourseDTO getCourseById(@PathVariable("id") UUID id) {
        return new CourseDTO((courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("No course found"))));
    }

    /*
    @RequestMapping("/assignments")
    public Assiignment getAssignmentsbyCourseID(){

    }
     */
}
