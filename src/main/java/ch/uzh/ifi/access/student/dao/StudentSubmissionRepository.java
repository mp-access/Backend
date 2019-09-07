package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.model.StudentSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubmissionRepository extends MongoRepository<StudentSubmission, String>, CustomizedStudentSubmissionRepository {

    <T extends StudentSubmission> List<T> findAllByExerciseIdAndUserIdAndIsGradedOrderByVersionDesc(String exerciseId, String userId, boolean isGraded);

    <T extends StudentSubmission> Optional<T> findTopByExerciseIdAndUserIdOrderByVersionDesc(String exerciseId, String userId);

    int countByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrue(String exerciseId, String userId);

    // db.getCollection('studentSubmissions').find({ "timestamp": { $gt: {$subtract: [ new Date(), 100 * 60 * 1000 ] } } })
    //  { $and: [ {"userId": "b2298423-4c12-426e-87fb-6cf1c50a48e8"}, { "console": { $exists: false}}, { "result": { $exists: false}}]}
    @Query("{ $and: [ {'userId': ?0}, { 'console': { $exists: false}}, { 'result': { $exists: false}}]}")
    <T extends StudentSubmission> List<T> findByUserIdAndHasNoResultOrConsoleNotOlderThan10min(String userId);

}
