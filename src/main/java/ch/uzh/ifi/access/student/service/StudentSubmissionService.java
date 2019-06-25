package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentSubmissionService {

    private final StudentSubmissionRepository studentSubmissionRepository;

    public StudentSubmissionService(StudentSubmissionRepository studentSubmissionRepository) {
        this.studentSubmissionRepository = studentSubmissionRepository;
    }

    public List<StudentSubmission> findAll() {
        return studentSubmissionRepository.findAll();
    }

    public Optional<StudentSubmission> findById(String submissionId) {
        Assert.notNull(submissionId, "Cannot get by id: id is null");
        return studentSubmissionRepository.findById(submissionId);
    }

    public <T extends StudentSubmission> List<T> findAllSubmissionsByExerciseAndUserOrderedByVersionDesc(String exerciseId, String userId) {
        Assert.notNull(exerciseId, "exerciseId cannot be null");
        Assert.notNull(userId, "userId cannot be null");

        return studentSubmissionRepository.findAllByExerciseIdAndUserIdOrderByVersionDesc(exerciseId, userId);
    }

    public <T extends StudentSubmission> T saveSubmission(T answer) {
        Assert.notNull(answer, "answer cannot be null");
        Assert.notNull(answer.getExerciseId(), "exerciseId cannot be null");
        Assert.notNull(answer.getUserId(), "userId cannot be null");

        Optional<StudentSubmission> previousSubmissions = findLatestExerciseSubmission(answer.getExerciseId(), answer.getUserId());
        previousSubmissions.ifPresent(prev -> answer.setVersion(prev.getVersion() + 1));

        return studentSubmissionRepository.save(answer);
    }

    public <T extends StudentSubmission> Optional<T> findLatestExerciseSubmission(String exerciseId, String userId) {
        Assert.notNull(exerciseId, "exerciseId cannot be null");
        Assert.notNull(userId, "userId cannot be null");

        return studentSubmissionRepository.findTopByExerciseIdAndUserIdOrderByVersionDesc(exerciseId, userId);
    }

    public List<StudentSubmission> findLatestSubmissionsByAssignment(Assignment assignment, String userId) {
        Assert.notNull(assignment, "assignment cannot be null");
        Assert.notNull(userId, "userId cannot be null");
        List<String> exerciseIds = assignment.getExercises().stream().map(Exercise::getId).collect(Collectors.toList());

        return studentSubmissionRepository.findByExerciseIdInAndUserIdOrderByVersionDesc(exerciseIds, userId);
    }

}
