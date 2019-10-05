package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.model.StudentSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubmissionRepository extends MongoRepository<StudentSubmission, String>, CustomizedStudentSubmissionRepository {

    <T extends StudentSubmission> List<T> findAllByExerciseIdAndUserIdAndIsGradedOrderByVersionDesc(String exerciseId, String userId, boolean isGraded);

    <T extends StudentSubmission> Optional<T> findTopByExerciseIdAndUserIdOrderByVersionDesc(String exerciseId, String userId);

    int countByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(String exerciseId, String userId);
}
