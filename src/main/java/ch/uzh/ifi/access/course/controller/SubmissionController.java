package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.course.dto.SubmissionHistoryDTO;
import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;
import ch.uzh.ifi.access.course.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final static Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final StudentSubmissionService studentSubmissionService;

    public SubmissionController(StudentSubmissionService studentSubmissionService) {
        this.studentSubmissionService = studentSubmissionService;
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
    public StudentSubmission submitExercise(@PathVariable String exerciseId, @RequestBody StudentAnswerDTO submissionDTO, CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        String username = authentication.getName();

        logger.info(String.format("User %s submitted exercise: %s", username, exerciseId));

        StudentSubmission submission = submissionDTO.createSubmission(authentication.getUserId(), exerciseId);
        return studentSubmissionService.saveSubmission(submission);
    }

    @GetMapping("/{exerciseId}/history")
    public SubmissionHistoryDTO getAllSubmissionsForExercise(@PathVariable String exerciseId, CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        logger.info(String.format("Fetching all submission for user %s and exercise %s", authentication.getName(), exerciseId));

        List<StudentSubmission> submissions = studentSubmissionService.findAllSubmissionsByExerciseAndUserOrderedByVersionDesc(exerciseId, authentication.getUserId());
        return new SubmissionHistoryDTO(submissions);
    }
}
