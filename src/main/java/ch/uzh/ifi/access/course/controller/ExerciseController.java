package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.dto.ExerciseWithSolutionsDTO;
import ch.uzh.ifi.access.course.model.Exercise;
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
import java.util.Optional;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final CourseService courseService;

    public ExerciseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> getExerciseByCourseAndAssignment(
            @PathVariable("exerciseId") String exerciseId) {
        Exercise ex = courseService.getExerciseById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("No exercise found for id"));
        if (ex.isPastDueDate()) {
            return ResponseEntity.ok(new ExerciseWithSolutionsDTO(ex));
        } else {
            return ResponseEntity.ok(ex);
        }
    }

    //TODO: Check if user has access to private & solution files
    //TODO: Check if due date is up
    @GetMapping("/{exerciseId}/files/{fileId}")
    public ResponseEntity<Resource> getFile(
            @PathVariable("exerciseId") String exerciseId,
            @PathVariable("fileId") String fileId) throws IOException {
        Optional<FileSystemResource> file = courseService.getFileByExerciseIdAndFileId(exerciseId, fileId);
        if (file.isPresent()) {
            File fileHandle = file.get().getFile();
            FileSystemResource r = new FileSystemResource(fileHandle);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(fileHandle.toPath())))
                    .body(r);
        }
        return ResponseEntity.notFound().build();
    }
}
