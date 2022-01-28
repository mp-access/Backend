package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.dto.ExerciseWithSolutionsDTO;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.course.service.CourseService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final CourseService courseService;

    public ExerciseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Return a response containing an exercise without solutions if the user who made the request can view the exercise.
     * @param exerciseId  requested exercise ID
     * @return            status OK with Exercise body, if accessible and found
     * @see CourseService#getExerciseWithViewPermission(String)  for permission filtering and exceptions
     */
    @GetMapping("/{exerciseId}")
    public ResponseEntity<Exercise> getExercise(@PathVariable String exerciseId) {
        return ResponseEntity.ok(courseService.getExerciseWithViewPermission(exerciseId));
    }

    /**
     * Return a response containing an exercise with solutions (and other private files) if the user who made the
     * request can view the exercise and has an assistant role for the exercise's course.
     * @param exerciseId  requested exercise ID
     * @return            status OK with ExerciseWithSolutionsDTO body, if accessible and found
     * @see CourseService#getExerciseWithViewPermission(String)  for permission filtering and exceptions
     */
    @GetMapping("/{exerciseId}/solutions")
    @PreAuthorize("hasRole('assistant')")
    public ResponseEntity<ExerciseWithSolutionsDTO> getExerciseWithSolutions(@PathVariable String exerciseId) {
        return ResponseEntity.ok(new ExerciseWithSolutionsDTO(courseService.getExerciseWithViewPermission(exerciseId)));
    }

    /**
     * Get a response containing a file associated with a specific exercise if the user who made the request can
     * view the exercise and the file exists. The function first tries to match the fileId input against the IDs of
     * all available files, then - if not found - against the names (without extension) of the files.
     * @param exerciseId  requested exercise ID
     * @param fileId      requested file ID or name without extension
     * @return            status OK with Resource body, if the exercise is accessible and the file was found
     * @throws ResourceNotFoundException  if the exercise exists but the file was not found
     * @see CourseService#getExerciseWithViewPermission(String)  for permission filtering and exceptions
     */
    @GetMapping("/{exerciseId}/files/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable String exerciseId, @PathVariable String fileId) {
        Exercise exercise = courseService.getExerciseWithViewPermission(exerciseId);

        VirtualFile virtualFile = exercise.getPublicOrResourcesFile(fileId)
                .orElseGet(() -> exercise.searchPublicOrResourcesFileByName(fileId)
                        .orElseThrow(() -> new ResourceNotFoundException("No file found for identifier")));

        FileSystemResource file = new FileSystemResource(virtualFile.getFile());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
    }
}