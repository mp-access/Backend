package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.course.dto.SubmissionHistoryDTO;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.course.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final static Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final StudentSubmissionService studentSubmissionService;

    private final CourseService courseService;

    public SubmissionController(StudentSubmissionService studentSubmissionService, CourseService courseService) {
        this.studentSubmissionService = studentSubmissionService;
        this.courseService = courseService;
    }

    @GetMapping("/{exerciseId}")
    public StudentSubmission getSubmissionByExercise(@PathVariable String exerciseId, CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");
        String username = authentication.getName();
        String userId = authentication.getUserId();

        logger.info(String.format("Fetching submission for user %s", username));

        return studentSubmissionService
                .findLatestExerciseSubmission(exerciseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cannot find any submission for user %s and exercise %s", userId, exerciseId)));
    }

    @PostMapping("/{exerciseId}")
    public ResponseEntity<?> submitExercise(@PathVariable String exerciseId, @RequestBody StudentAnswerDTO submissionDTO, CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        String username = authentication.getName();

        logger.info(String.format("User %s submitted exercise: %s", username, exerciseId));

        Optional<String> commitHash = courseService.getExerciseById(exerciseId).map(Exercise::getGitHash);

        if (commitHash.isPresent()) {
            StudentSubmission submission = submissionDTO.createSubmission(authentication.getUserId(), exerciseId, commitHash.get());
            return ResponseEntity.accepted().body(studentSubmissionService.saveSubmission(submission));
        } else {
            return ResponseEntity.badRequest().body("Referenced exercise does not exist");
        }
    }

    @GetMapping("/{exerciseId}/history")
    public SubmissionHistoryDTO getAllSubmissionsForExercise(@PathVariable String exerciseId, CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        logger.info(String.format("Fetching all submission for user %s and exercise %s", authentication.getName(), exerciseId));

        List<StudentSubmission> submissions = studentSubmissionService.findAllSubmissionsByExerciseAndUserOrderedByVersionDesc(exerciseId, authentication.getUserId());
        return new SubmissionHistoryDTO(submissions);
    }
}
