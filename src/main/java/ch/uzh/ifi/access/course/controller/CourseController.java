package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.Model.Exercise;
import ch.uzh.ifi.access.course.Model.FileContent;
import ch.uzh.ifi.access.course.dto.AssignmentDTO;
import ch.uzh.ifi.access.course.dto.CourseDTO;
import ch.uzh.ifi.access.course.service.CourseService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

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

    @GetMapping("/{courseId}/assignments/{assignmentId}/exercises/{exerciseId}")
    public Exercise getExerciseByCourseAndAssignment(@PathVariable("courseId") UUID courseId,
                                                     @PathVariable("assignmentId") UUID assignmentId,
                                                     @PathVariable("exerciseId") UUID exerciseId) {
        return courseService.getCourseById(courseId)
                .map(course -> course.getAssignmentById(assignmentId))
                .map(a -> a.orElseThrow(IllegalArgumentException::new))
                .map(a -> a.findExerciseById(exerciseId))
                .map(exercise -> exercise.orElseThrow(IllegalArgumentException::new))
                .orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping("/{courseId}/assignments/{assignmentId}/exercises/{exerciseId}/files/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable("courseId") UUID courseId,
                                            @PathVariable("assignmentId") UUID assignmentId,
                                            @PathVariable("exerciseId") UUID exerciseId,
                                            @PathVariable("fileId") UUID fileId) throws IOException {
        Exercise e = getExerciseByCourseAndAssignment(courseId, assignmentId, exerciseId);
        Optional<FileContent> f = e.getFileById(fileId);
        if (f.isPresent()) {
            File fileHandle = f.get().getFile();
            FileSystemResource r = new FileSystemResource(fileHandle);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(fileHandle.toPath())))
                    .body(r);
        }
        return ResponseEntity.notFound().build();
    }

}
