package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminSubmissionService {

    private final StudentSubmissionRepository repository;

    private final CourseDAO courseRepository;

    public AdminSubmissionService(StudentSubmissionRepository repository, CourseDAO courseRepository) {
        this.repository = repository;
        this.courseRepository = courseRepository;
    }

    public AssignmentReport generateAssignmentReport(final String courseId, final String assignmentId) {

        return null;
    }

    @Data
    public static class AssignmentReport {

        private final String assignmentId;

        //        private Table<String, String, StudentSubmission> submissionByExerciseIdAndUserId;
        private Map<String, Map<String, StudentSubmission>> submissionByExerciseIdAndUserId;

        public AssignmentReport(Assignment assignment, List<String> userEmailAddresses) {
            this.assignmentId = assignment.getId();
            this.submissionByExerciseIdAndUserId = new HashMap<>();

            assignment.getExercises().forEach(exercise -> this.submissionByExerciseIdAndUserId.put(exercise.getId(), new HashMap<>()));
        }
    }
}
