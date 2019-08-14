package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.model.StudentSubmission;

import java.util.List;

interface CustomizedStudentSubmissionRepository {
    List<StudentSubmission> findByExerciseIdInAndUserIdOrderByVersionDesc(List<String> exerciseIds, String userId);

    void invalidateSubmissionsByExerciseId(String exerciseId);
}