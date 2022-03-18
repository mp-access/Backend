package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.dto.UserMigrationResult;

interface CustomizedStudentSubmissionRepository {
    void invalidateSubmissionsByExerciseId(String exerciseId);

    void invalidateSubmissionsByExerciseIdAndUserId(String exerciseId, String userId);

    boolean existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(String userId);

    UserMigrationResult migrateUserSubmissions(String from, String to);
}