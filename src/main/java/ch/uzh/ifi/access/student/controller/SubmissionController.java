package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.config.CoursePermissionEvaluator;
import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.student.dto.SubmissionCount;
import ch.uzh.ifi.access.student.dto.SubmissionHistoryDTO;
import ch.uzh.ifi.access.student.evaluation.EvalProcessService;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final static Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final StudentSubmissionService studentSubmissionService;

    private final CourseService courseService;

    private final EvalProcessService processService;

    private final CoursePermissionEvaluator permissionEvaluator;

    public SubmissionController(StudentSubmissionService studentSubmissionService, CourseService courseService, EvalProcessService processService, CoursePermissionEvaluator permissionEvaluator) {
        this.studentSubmissionService = studentSubmissionService;
        this.courseService = courseService;
        this.processService = processService;
        this.permissionEvaluator = permissionEvaluator;
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

    @PostMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> submit(@PathVariable String exerciseId, @RequestBody StudentAnswerDTO submissionDTO, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        String username = authentication.getName();

        logger.info(String.format("User %s submitted exercise: %s", username, exerciseId));

        if (studentSubmissionService.hasUserCurrentlyRunningSubmissions(authentication.getUserId())) {
            return new ResponseEntity<>(
                    "Submition rejected: User has an other running submisison.", HttpStatus.TOO_MANY_REQUESTS);
        }

        Exercise exercise = courseService.getExerciseById(exerciseId).orElseThrow(() -> new ResourceNotFoundException("Referenced exercise does not exist"));

        if (permissionEvaluator.hasAccessToExercise(authentication, exercise)) {

            String commitHash = exercise.getGitHash();
            if (StringUtils.isEmpty(commitHash)) {
                return ResponseEntity.badRequest().body("Referenced exercise does not exist");
            }

            StudentSubmission submission = submissionDTO.createSubmission(authentication.getUserId(), exerciseId, commitHash);

            if (exercise.isPastDueDate() && submission.isGraded()) {
                return ResponseEntity.unprocessableEntity().body("Submission is past due date. Cannot accept a graded submission anymore.");
            }

            submission = studentSubmissionService.initSubmission(submission);
            String processId = processService.initEvalProcess(submission);
            processService.fireEvalProcessExecutionAsync(processId);

            return ResponseEntity.ok().body(new AbstractMap.SimpleEntry<>("evalId", processId));
        }

        return ResponseEntity.badRequest().body("Exercise is not yet online!");
    }

    @GetMapping("/evals/{processId}")
    public Map<String, String> getEvalProcessState(@PathVariable String processId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");
        Assert.notNull(processId, "No processId.");
        return processService.getEvalProcessState(processId);
    }

    @GetMapping("/exercises/{exerciseId}/history")
    public SubmissionHistoryDTO getAllSubmissionsForExercise(@PathVariable String exerciseId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        logger.info(String.format("Fetching all submission for user %s and exercise %s", authentication.getName(), exerciseId));

        List<StudentSubmission> runs = studentSubmissionService.findAllSubmissionsByExerciseAndUserAndIsGradedOrderedByVersionDesc(exerciseId, authentication.getUserId(), false);
        List<StudentSubmission> submissions = studentSubmissionService.findAllSubmissionsByExerciseAndUserAndIsGradedOrderedByVersionDesc(exerciseId, authentication.getUserId(), true);
        SubmissionCount submissionCount = getAvailableSubmissionCount(exerciseId, authentication);
        Optional<Exercise> exercise = courseService.getExerciseById(exerciseId);
        boolean isPastDueDate = exercise.map(Exercise::isPastDueDate).orElse(false);
        LocalDateTime dueDate = exercise.map(Exercise::getDueDate).orElse(null);

        return new SubmissionHistoryDTO(submissions, runs, submissionCount, dueDate, isPastDueDate);
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
