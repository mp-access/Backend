package ch.uzh.ifi.access.student.reporting;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.User;
import lombok.Value;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Value
public class AssignmentReport {

    private final String assignmentId;

    private final Map<String, Map<String, SubmissionEvaluation>> exercises;

    public AssignmentReport(Assignment assignment, List<User> students, Map<User, List<StudentSubmission>> assignmentSubmissionsByUser) {
        this.assignmentId = assignment.getId();
        this.exercises = new LinkedHashMap<>(assignment.getExercises().size());

        // Sort lexicographically to ensure students always occur in the same order in the report
        List<User> sortedStudents = students.stream().sorted().collect(Collectors.toList());

        assignment.getExercises().forEach(exercise -> {
            Map<String, SubmissionEvaluation> exerciseSubmissionsByStudentEmail = new LinkedHashMap<>(students.size());
            sortedStudents.forEach(user -> exerciseSubmissionsByStudentEmail.put(user.getEmailAddress(), SubmissionEvaluation.NO_SUBMISSION));

            this.exercises.put(exercise.getId(), exerciseSubmissionsByStudentEmail);
        });

        for (var entry : assignmentSubmissionsByUser.entrySet()) {
            var user = entry.getKey();
            var submissions = entry.getValue();
            for (var submission : submissions) {
                var exerciseId = submission.getExerciseId();
                this.exercises.get(exerciseId).put(user.getEmailAddress(), submission.getResult());
            }
        }
    }
}