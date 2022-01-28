package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.student.SubmissionProperties;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.dto.UserMigrationResult;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentSubmissionService {

    private StudentSubmissionRepository studentSubmissionRepository;

    private SubmissionProperties submissionProperties;

    public StudentSubmissionService(StudentSubmissionRepository studentSubmissionRepository, SubmissionProperties submissionProperties) {
        this.studentSubmissionRepository = studentSubmissionRepository;
        this.submissionProperties = submissionProperties;
    }

    public List<StudentSubmission> findAll() {
        return studentSubmissionRepository.findAll();
    }

    public Optional<StudentSubmission> findById(String submissionId) {
        Assert.notNull(submissionId, "Cannot get by id: id is null");
        return studentSubmissionRepository.findById(submissionId);
    }

    /**
     * Get a submission by its ID and return it only if the user who made the request satisfies one of the conditions:
     * (1) Is the same user as the one who posted the submission
     * (2) Has an assistant role for the submission's course
     * @param submissionId  requested submission ID
     * @return              StudentSubmission object matching the requested ID, if accessible and found
     * @throws AccessDeniedException      if the user does not meet any of the conditions to access the submission
     * @throws ResourceNotFoundException  if the submission was not found
     */
    @PostAuthorize("(authentication.name == returnObject.userId) or hasRole(returnObject.courseId + '-assistant')")
    public StudentSubmission getSubmissionWithPermission(String submissionId) {
        return findById(submissionId).orElseThrow(() -> new ResourceNotFoundException("No submission found"));
    }

    public <T extends StudentSubmission> List<T> findAllGradedSubmissions(String exerciseId, String userId) {
        Assert.notNull(exerciseId, "exerciseId cannot be null");
        Assert.notNull(userId, "userId cannot be null");
        return studentSubmissionRepository
                .findAllByExerciseIdAndUserIdAndIsGradedOrderByVersionDesc(exerciseId, userId, true);
    }

    public <T extends StudentSubmission> List<T> findAllTestRuns(String exerciseId, String userId) {
        Assert.notNull(exerciseId, "exerciseId cannot be null");
        Assert.notNull(userId, "userId cannot be null");
        return studentSubmissionRepository
                .findAllByExerciseIdAndUserIdAndIsGradedOrderByVersionDesc(exerciseId, userId, false);
    }

    public <T extends StudentSubmission> T saveSubmission(T answer) {
        Assert.notNull(answer, "answer cannot be null");
        Assert.notNull(answer.getExerciseId(), "exerciseId cannot be null");
        Assert.notNull(answer.getUserId(), "userId cannot be null");

        return studentSubmissionRepository.save(answer);
    }

    public <T extends StudentSubmission> T initSubmission(T answer) {
        Assert.notNull(answer, "answer cannot be null");
        Assert.notNull(answer.getExerciseId(), "exerciseId cannot be null");
        Assert.notNull(answer.getUserId(), "userId cannot be null");

        Optional<StudentSubmission> previousSubmissions = findLatestExerciseSubmission(answer.getExerciseId(), answer.getUserId());
        previousSubmissions.ifPresent(prev -> answer.setVersion(prev.getVersion() + 1));

        if (!(answer instanceof CodeSubmission)) {
            answer.setGraded(true);
        }

        return studentSubmissionRepository.save(answer);
    }

    public <T extends StudentSubmission> Optional<T> findLatestExerciseSubmission(String exerciseId, String userId) {
        Assert.notNull(exerciseId, "exerciseId cannot be null");
        Assert.notNull(userId, "userId cannot be null");

        return studentSubmissionRepository.findTopByExerciseIdAndUserIdOrderByVersionDesc(exerciseId, userId);
    }

    /**
     * For a given assignment and user returns the last graded submitted version of each exercise.
     * Aggregation procedure:
     * 1. Find all submissions by exerciseId and userId
     * 2. Sort them by version descending
     * 3. Take the top result (the one with the highest version number == most recent)
     * @param assignment    containing the exercises to aggregate by
     * @param userId        requested user ID
     * @return              list containing the most recent submission for each exercise, if exists
     */
    public List<StudentSubmission> findLatestGradedSubmissionsByAssignment(Assignment assignment, String userId) {
        Assert.notNull(assignment, "assignment cannot be null");
        Assert.notNull(userId, "userId cannot be null");
        return assignment.getExercises().stream().map(exercise -> findLatestExerciseSubmission(exercise.getId(), userId))
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public List<StudentSubmission> findLatestGradedInvalidatedSubmissionsByAssignment(Assignment assignment, String userId) {
        return findLatestGradedSubmissionsByAssignment(assignment, userId).stream()
                .filter(submission -> submission != null && submission.isInvalid()).collect(Collectors.toList());
    }

    public void invalidateSubmissionsByExerciseIdIn(List<String> exerciseIds) {
        if (exerciseIds != null) {
            exerciseIds.forEach(this::invalidateSubmissionsByExerciseId);
        }
    }

    public void invalidateSubmissionsByExerciseId(String exerciseId) {
        studentSubmissionRepository.invalidateSubmissionsByExerciseId(exerciseId);
    }

    public void invalidateSubmissionsByExerciseAndUser(String exerciseId, String userId) {
        studentSubmissionRepository.invalidateSubmissionsByExerciseIdAndUserId(exerciseId, userId);
    }

    /**
     * Find all valid, graded submissions by a user for a specific exercise. If the user who made the request has a
     * student role for the course, all found submissions are returned; otherwise, an empty list is returned.
     * @param exerciseId    requested exercise ID
     * @param userId        ID of the user who posted the submission
     * @return              list of past StudentSubmissions, if existing and the user who made the request has a
     *                       student role, else an empty list
     */
    @PostFilter("hasRole(filterObject.courseId + '-student') or (authentication.name != #userId)")
    public List<StudentSubmission> filterValidSubmissionsByPermission(String exerciseId, String userId) {
        return studentSubmissionRepository
                .findAllByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(exerciseId, userId);
    }

    public boolean isUserRateLimited(String userId) {
        return submissionProperties.isUserRateLimit() && hasUserCurrentlyRunningSubmissions(userId);
    }

    public boolean hasUserCurrentlyRunningSubmissions(String userId) {
        return studentSubmissionRepository.existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(userId);
    }

    public UserMigrationResult migrateUserSubmissions(String from, String to) {
        return studentSubmissionRepository.migrateUserSubmissions(from, to);
    }
}
