package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.dto.UserMigrationResult;
import ch.uzh.ifi.access.student.model.StudentSubmission;

import java.util.List;

interface CustomizedStudentSubmissionRepository {
    List<StudentSubmission> findByExerciseIdInAndUserIdAndIsGradedOrderByVersionDesc(List<String> exerciseIds, String userId);

    void invalidateSubmissionsByExerciseId(String exerciseId);

    boolean existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(String userId);

    UserMigrationResult migrateUserSubmissions(String from, String to);
}