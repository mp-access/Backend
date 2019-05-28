package ch.uzh.ifi.access.course.model.workspace;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAnswerRepository extends MongoRepository<StudentAnswer, String> {
}
