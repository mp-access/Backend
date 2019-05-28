package ch.uzh.ifi.access.course.model.workspace;

import org.springframework.stereotype.Service;

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

    public <T extends StudentAnswer> T saveSubmission(T answer) {
        return studentAnswerRepository.save(answer);
    }
}
