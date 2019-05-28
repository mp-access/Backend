package ch.uzh.ifi.access.course.model.workspace;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends MongoRepository<StudentAnswer, String> {

    <T extends StudentAnswer> List<T> findAllByExerciseIdAndUserIdOrderByVersionDesc(String exerciseId, String userId);
}
