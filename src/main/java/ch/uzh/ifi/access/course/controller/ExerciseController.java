package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.config.CoursePermissionEvaluator;
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
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final CourseService courseService;

    private final CoursePermissionEvaluator permissionEvaluator;

    public ExerciseController(CourseService courseService, CoursePermissionEvaluator permissionEvaluator) {
        this.courseService = courseService;
        this.permissionEvaluator = permissionEvaluator;
    }

    @GetMapping("/{exerciseId}")
    public ResponseEntity<?> getExerciseByCourseAndAssignment(
            @PathVariable("exerciseId") String exerciseId, @ApiIgnore CourseAuthentication authentication) {
        Exercise exercise = courseService.getExerciseById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("No exercise found for id"));

        if (permissionEvaluator.hasAccessToExercise(authentication, exercise)) {
            if (hasAccessToExerciseSolutions(exercise, authentication)) {
                return ResponseEntity.ok(new ExerciseWithSolutionsDTO(exercise));
            } else {
                return ResponseEntity.ok(exercise);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{exerciseId}/files/{fileId}")
    public ResponseEntity<Resource> getFile(
            @PathVariable("exerciseId") String exerciseId,
            @PathVariable("fileId") String fileId,
            @ApiIgnore CourseAuthentication authentication) throws IOException {
        Exercise exercise = courseService.getExerciseById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("No exercise found for id"));

        if (permissionEvaluator.hasAccessToExercise(authentication, exercise)) {
            Optional<FileSystemResource> file = courseService.getFileCheckingPrivileges(exercise, fileId, authentication);

            if (file.isPresent()) {
                File fileHandle = file.get().getFile();
                FileSystemResource r = new FileSystemResource(fileHandle);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(Files.probeContentType(fileHandle.toPath())))
                        .body(r);
            }
        }

        return ResponseEntity.notFound().build();
    }

    private boolean hasAccessToExerciseSolutions(Exercise exercise, CourseAuthentication authentication) {
        return exercise.isPastDueDate() || authentication.hasAdminAccess(exercise.getCourseId());
    }
}
