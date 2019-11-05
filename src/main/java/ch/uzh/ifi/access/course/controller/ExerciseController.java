package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.config.CoursePermissionEvaluator;
import ch.uzh.ifi.access.course.dto.ExerciseWithSolutionsDTO;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.course.service.CourseService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.util.Map;
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
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(r);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{exerciseId}/files/search")
    public ResponseEntity<Resource> searchForFile(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody Map<String, String> body,
            @ApiIgnore CourseAuthentication authentication) throws IOException {
        Exercise exercise = courseService.getExerciseById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("No exercise found for id"));

        var filename = body.get("filename");
        if (permissionEvaluator.hasAccessToExercise(authentication, exercise) && filename != null) {
            if (filename.startsWith("resource/") || filename.startsWith("public/")) {

                filename = filename.split("/")[1];
            }

            Optional<VirtualFile> virtualFile = exercise.searchPublicOrResourcesFileByName(filename);
            Optional<FileSystemResource> file = virtualFile.map(vf -> new FileSystemResource(vf.getFile()));

            if (file.isPresent()) {
                File fileHandle = file.get().getFile();
                FileSystemResource r = new FileSystemResource(fileHandle);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(r);
            }
        }

        return ResponseEntity.notFound().build();
    }

    private boolean hasAccessToExerciseSolutions(Exercise exercise, CourseAuthentication authentication) {
        return exercise.isPastDueDate() || authentication.hasPrivilegedAccess(exercise.getCourseId());
    }
}
