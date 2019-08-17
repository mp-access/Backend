package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.student.dto.SubmissionHistoryDTO;
import ch.uzh.ifi.access.student.dto.SubmissionResult;
import ch.uzh.ifi.access.student.evaluation.EvalProcessService;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final static Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final StudentSubmissionService studentSubmissionService;

    private final CourseService courseService;

    private final EvalProcessService processService;

    public SubmissionController(StudentSubmissionService studentSubmissionService, CourseService courseService, EvalProcessService processService) {
        this.studentSubmissionService = studentSubmissionService;
        this.courseService = courseService;
        this.processService = processService;
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<StudentSubmission> getSubmissionById(@PathVariable String submissionId, @ApiIgnore CourseAuthentication authentication) {
        StudentSubmission submission = studentSubmissionService.findById(submissionId).orElse(null);

        if (submission == null || !submission.userIdMatches(authentication.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(submission);
    }

    @GetMapping("/exercises/{exerciseId}")
    public ResponseEntity<StudentSubmission> getSubmissionByExercise(@PathVariable String exerciseId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");
        String username = authentication.getName();
        String userId = authentication.getUserId();

        logger.info(String.format("Fetching submission for user %s", username));

        Optional<StudentSubmission> submission = studentSubmissionService
                .findLatestExerciseSubmission(exerciseId, userId);

        return submission
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/exs/{exerciseId}")
    public Map.Entry<String, String> submitEval(@PathVariable String exerciseId, @RequestBody StudentAnswerDTO submissionDTO, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        String username = authentication.getName();

        logger.info(String.format("User %s submitted exercise: %s", username, exerciseId));

        Optional<String> commitHash = courseService.getExerciseById(exerciseId).map(Exercise::getGitHash);
        String processId = "N/A";
        if (commitHash.isPresent()) {
            StudentSubmission submission = submissionDTO.createSubmission(authentication.getUserId(), exerciseId, commitHash.get());
            submission = studentSubmissionService.initSubmission(submission);
            processId = processService.initEvalProcess(submission);
            processService.fireEvalProcessExecutionAsync(processId);
        }
        return new AbstractMap.SimpleEntry<>("evalId", processId);
    }

    @GetMapping("/evals/{processId}")
    public Map<String, String> getEvalProcessState(@PathVariable String processId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");
        Assert.notNull(processId, "No processId.");
        return processService.getEvalProcessState(processId);
    }

    @PostMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> submitExercise(@PathVariable String exerciseId, @RequestBody StudentAnswerDTO submissionDTO, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        String username = authentication.getName();

        logger.info(String.format("User %s submitted exercise: %s", username, exerciseId));

        Optional<String> commitHash = courseService.getExerciseById(exerciseId).map(Exercise::getGitHash);

        if (commitHash.isPresent()) {
            StudentSubmission submission = submissionDTO.createSubmission(authentication.getUserId(), exerciseId, commitHash.get());
            return ResponseEntity.accepted().body(studentSubmissionService.initSubmission(submission));
        } else {
            return ResponseEntity.badRequest().body("Referenced exercise does not exist");
        }
    }

    @GetMapping("/exercises/{exerciseId}/history")
    public SubmissionHistoryDTO getAllSubmissionsForExercise(@PathVariable String exerciseId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        logger.info(String.format("Fetching all submission for user %s and exercise %s", authentication.getName(), exerciseId));

        List<StudentSubmission> submissions = studentSubmissionService.findAllSubmissionsByExerciseAndUserOrderedByVersionDesc(exerciseId, authentication.getUserId());
        return new SubmissionHistoryDTO(submissions);
    }

    @GetMapping("/attempts/exercises/{exerciseId}/")
    public SubmissionCount getAvailableSubmissionCount(@PathVariable String exerciseId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        Integer maxSubmissions = courseService
                .getExerciseMaxSubmissions(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Referenced exercise does not exist"));

        int validSubmissionCount = studentSubmissionService.getSubmissionCountByExerciseAndUser(exerciseId, authentication.getUserId());
        return new SubmissionCount(maxSubmissions, validSubmissionCount);
    }
}
