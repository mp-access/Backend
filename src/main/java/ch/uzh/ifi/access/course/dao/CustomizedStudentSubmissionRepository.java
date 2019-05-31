package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;

import java.util.List;

interface CustomizedStudentSubmissionRepository {
    List<StudentSubmission> findByExerciseIdInAndUserIdOrderByVersionDesc(List<String> exerciseIds, String userId);
}