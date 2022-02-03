package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.student.dto.SubmissionCount;
import ch.uzh.ifi.access.student.dto.SubmissionHistoryDTO;
import ch.uzh.ifi.access.student.evaluation.EvalProcessService;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/exercises/{exerciseId}/submissions")
public class SubmissionController {

    private final StudentSubmissionService studentSubmissionService;

    private final CourseService courseService;

    private final EvalProcessService processService;

    public SubmissionController(StudentSubmissionService studentSubmissionService, CourseService courseService,
                                EvalProcessService processService) {
        this.studentSubmissionService = studentSubmissionService;
        this.courseService = courseService;
        this.processService = processService;
    }

    /**
     * Get a submission by its ID if the user who made the request can access the submission.
     * @param exerciseId    exercise ID of the requested submission
     * @param submissionId  requested submission ID
     * @return              status OK with StudentSubmission body, if accessible and found
     * @see StudentSubmissionService#getSubmissionWithPermission(String)  for permission filtering and exceptions
     */
    @GetMapping("/{submissionId}")
    public ResponseEntity<StudentSubmission> getSubmissionById(@PathVariable String exerciseId, @PathVariable String submissionId) {
        log.info("Fetching submission ID {} for exercise '{}'", submissionId, exerciseId);
        return ResponseEntity.ok(studentSubmissionService.getSubmissionWithPermission(submissionId));
    }

    /**
     * Get the latest submission made by a user for a specific exercise if exists and the user who made the request
     * can access the submission, else return an empty response.
     * @param exerciseId    requested exercise ID
     * @param userId        ID of the user who posted the submission
     * @return              status OK with StudentSubmission body, if accessible and found, else without content
     * @see #getSubmissionById(String, String)
     */
    @GetMapping("/users/{userId}/latest")
    public ResponseEntity<StudentSubmission> getLatestSubmissionByExercise(@PathVariable String exerciseId, @PathVariable String userId) {
        log.info("Fetching latest submission by user {} for exercise {}", userId, exerciseId);
        Optional<StudentSubmission> submission = studentSubmissionService.findLatestExerciseSubmission(exerciseId, userId);
        if (submission.isPresent())
            return getSubmissionById(exerciseId, submission.get().getId()); // Verify the user can access the submission
        return ResponseEntity.noContent().build();
    }

    /**
     * Initialise evaluation of a solution to an exercise posted by the user who made the request.
     * @param submissionDTO  solution posted by the user
     * @param exerciseId     solved exercise ID
     * @param userId         ID of the submitting user, must match the name of the user who made the request
     * @return               status OK with the evaluation process ID, if the submission can be evaluated, or
     *                        status 429 if the user currently has a submission running or exhausted their attempts
     * @see #getValidSubmissionCountWithPermission(Exercise, String)  for counting past attempts
     * @see CourseService#getExerciseWithPermission(String, boolean)  for permission filtering and exceptions
     */
    @PostMapping("/users/{userId}/submit")
    @PreAuthorize("authentication.name == #userId")
    public ResponseEntity<String> submit(@RequestBody StudentAnswerDTO submissionDTO, @PathVariable String exerciseId, @PathVariable String userId) {
        log.info("Testing submission by user {} for exercise {}", userId, exerciseId);

        if (studentSubmissionService.isUserRateLimited(userId))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Submission rejected: User has an other running submission.");

        StudentSubmission submission = submissionDTO.createSubmission(exerciseId, userId);
        Exercise exercise = courseService.getExerciseWithPermission(exerciseId, submission.isGraded());
        SubmissionCount submissionCount = getValidSubmissionCountWithPermission(exercise, userId);

        if (submission.isGraded() && submissionCount.getSubmissionsRemaining() <= 0)
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("You have exhausted your attempts for this exercise");

        StudentSubmission savedSubmission = studentSubmissionService.initSubmission(submission);
        String processId = processService.initEvalProcess(savedSubmission);
        processService.fireEvalProcessExecutionAsync(processId);

        return ResponseEntity.ok().body(processId);
    }

    @GetMapping("/eval/{processId}")
    public Map<String, String> getEvalProcessState(@PathVariable String exerciseId, @PathVariable String processId) {
        log.info("Checking evaluation process state for exercise {}", exerciseId);
        return processService.getEvalProcessState(processId);
    }

    /**
     * Return the entire submission history of a user for a specific exercise.
     * @param exerciseId    requested exercise ID
     * @param userId        ID of the user who posted the submission
     * @return              status OK with SubmissionHistoryDTO body, if accessible and found
     * @see #getValidSubmissionCountWithPermission(Exercise, String)  for counting past attempts
     * @see CourseService#getExerciseWithViewPermission(String)  for permission filtering and exceptions
     */
    @GetMapping("/users/{userId}/history")
    public SubmissionHistoryDTO getSubmissionHistoryForExercise(@PathVariable String exerciseId, @PathVariable String userId) {
        log.info("Fetching all submissions for user {} and exercise {}", userId, exerciseId);

        List<StudentSubmission> testRuns = studentSubmissionService.findAllTestRuns(exerciseId, userId);
        List<StudentSubmission> submissions = studentSubmissionService.findAllGradedSubmissions(exerciseId, userId);
        Exercise exercise = courseService.getExerciseWithViewPermission(exerciseId);
        SubmissionCount submissionCount = getValidSubmissionCountWithPermission(exercise, userId);

        return new SubmissionHistoryDTO(submissions, testRuns, submissionCount);
    }

    /**
     * Check whether the user satisfies the conditions for accessing any submission posted by the input userId, and
     * if yes get the list of filtered valid submissions for the user and exercise; the number of past, valid, graded
     * submissions (i.e. the number of graded submission attempts so far) is defined as the size of the returned list.
     * @param exercise    requested exercise ID
     * @param userId      ID of the user who posted the submission
     * @return            SubmissionCount with the maximal and current number of attempts for the user and exercise
     * @throws AccessDeniedException   if the user cannot access a submission made by the input userId
     * @see StudentSubmissionService#filterValidSubmissionsByPermission(String, String)   for filtering past submissions
     * @see StudentSubmissionService#getSubmissionWithPermission(String)   for the conditions for accessing a submission
     */
    @PreAuthorize("(authentication.name == #userId) or hasRole(#exercise.courseId + '-assistant')")
    public SubmissionCount getValidSubmissionCountWithPermission(Exercise exercise, String userId) {
        int validSubmissionsCount = studentSubmissionService.filterValidSubmissionsByPermission(exercise.getId(), userId).size();
        return new SubmissionCount(exercise.getMaxSubmits(), validSubmissionsCount);
    }
}
