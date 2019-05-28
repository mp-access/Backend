package ch.uzh.ifi.access.course.model.workspace;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class StudentAnswerService {

    private final StudentAnswerRepository studentAnswerRepository;

    public StudentAnswerService(StudentAnswerRepository studentAnswerRepository) {
        this.studentAnswerRepository = studentAnswerRepository;
    }

    public List<StudentAnswer> findAll() {
        return studentAnswerRepository.findAll();
    }

    public <T extends StudentAnswer> List<T> findAllSubmissionsOrderedByVersionDesc(String exerciseId, String userId) {
        Assert.notNull(exerciseId, "exerciseId cannot be null");
        Assert.notNull(userId, "userId cannot be null");
        
        return studentAnswerRepository.findAllByExerciseIdAndUserIdOrderByVersionDesc(exerciseId, userId);
    }

    public <T extends StudentAnswer> T saveSubmission(T answer) {
        Assert.notNull(answer, "answer cannot be null");

        List<StudentAnswer> previousSubmissions = findAllSubmissionsOrderedByVersionDesc(answer.getExerciseId(), answer.getUserId());
        if (previousSubmissions.size() > 0) {
            StudentAnswer lastUserSubmission = previousSubmissions.get(0);
            answer.setVersion(lastUserSubmission.getVersion() + 1);
        }

        return studentAnswerRepository.save(answer);
    }
}
