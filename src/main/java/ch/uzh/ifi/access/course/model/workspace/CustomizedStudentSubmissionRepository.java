package ch.uzh.ifi.access.course.model.workspace;

import java.util.List;

interface CustomizedStudentSubmissionRepository {
    List<StudentSubmission> findByExerciseIdInAndUserIdOrderByVersionDesc(List<String> exerciseIds, String userId);
}