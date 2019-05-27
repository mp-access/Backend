package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.dto.CourseMetadataDTO;
import ch.uzh.ifi.access.course.dto.ExerciseWithSolutionsDTO;
import ch.uzh.ifi.access.course.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.course.model.workspace.StudentAnswer;
import ch.uzh.ifi.access.course.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

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


    @GetMapping("/{courseId}/assignments/{assignmentId}")
    public AssignmentMetadataDTO getAssignmentByCourseId(@PathVariable("courseId") String courseId, @PathVariable("assignmentId") String assignmentId) {
        return new AssignmentMetadataDTO(courseService.getCourseById(courseId)
                .flatMap(course -> course.getAssignmentById(assignmentId))
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found")));
    }

    @GetMapping("/{courseId}/assignments/{assignmentId}/exercises/{exerciseId}")
    public ResponseEntity<?> getExerciseByCourseAndAssignment(@PathVariable("courseId") String courseId,
                                                              @PathVariable("assignmentId") String assignmentId,
                                                              @PathVariable("exerciseId") String exerciseId) {
        Exercise ex = courseService.getExerciseByCourseAndAssignmentId(courseId, assignmentId, exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("No exercise found for id"));
        if (ex.isPastDueDate()) {
            return ResponseEntity.ok(new ExerciseWithSolutionsDTO(ex));
        } else {
            return ResponseEntity.ok(ex);
        }
    }

    @GetMapping("/{courseId}/assignments/{assignmentId}/exercises/{exerciseId}/files/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable("courseId") String courseId,
                                            @PathVariable("assignmentId") String assignmentId,
                                            @PathVariable("exerciseId") String exerciseId,
                                            @PathVariable("fileId") String fileId) throws IOException {
        Exercise e = courseService.getExerciseByCourseAndAssignmentId(courseId, assignmentId, exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("No exercise found for id"));

        //TODO: Check if user has access to private & solution files
        //TODO: Check if due date is up
        Optional<VirtualFile> f = e.getFileById(fileId);
        if (f.isPresent()) {
            File fileHandle = f.get().getFile();
            FileSystemResource r = new FileSystemResource(fileHandle);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(fileHandle.toPath())))
                    .body(r);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{courseId}/assignments/{assignmentId}/exercises/{exerciseId}")
    public ResponseEntity<StudentAnswer> submitAnswer(@PathVariable("courseId") String courseId,
                                                      @PathVariable("assignmentId") String assignmentId,
                                                      @PathVariable("exerciseId") String exerciseId,
                                                      @RequestBody StudentAnswerDTO studentAnswer,
                                                      CourseAuthentication authentication) {
        Exercise exercise = courseService.getExerciseByCourseAndAssignmentId(courseId, assignmentId, exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("No exercise found for id"));

        StudentAnswer workspace = studentAnswer.createStudentAnswer();
        workspace.setExercise(exercise);
        workspace.setAssignmentId(assignmentId);
        workspace.setCourseId(courseId);
        workspace.setTimestamp(LocalDateTime.now());
        workspace.setVersion(1);
        workspace.setCommitId("commit-qwertz");

        if (authentication != null) {
            workspace.setUserId(authentication.getUserId());
        }

        return ResponseEntity.ok(workspace);
    }
}
