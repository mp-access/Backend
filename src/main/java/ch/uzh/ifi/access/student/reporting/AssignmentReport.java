package ch.uzh.ifi.access.student.reporting;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Exercise;
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

    private final Map<String, Map<String, SubmissionEvaluation>> byExercises;

    private final Map<String, Map<String, SubmissionEvaluation>> byStudents;

    private final Map<String, Double> totalsByStudent;

    private final List<String> exerciseIds;

    private final List<String> students;

    public AssignmentReport(Assignment assignment, List<User> students, Map<User, List<StudentSubmission>> assignmentSubmissionsByUser) {
        this.assignmentId = assignment.getId();
        this.byExercises = new LinkedHashMap<>(assignment.getExercises().size());
        this.byStudents = new LinkedHashMap<>(students.size());
        this.totalsByStudent = new LinkedHashMap<>(students.size());

        // Sort lexicographically to ensure students always occur in the same order in the report
        List<User> sortedStudents = students.stream().sorted().collect(Collectors.toList());
        this.students = sortedStudents.stream().map(User::getEmailAddress).collect(Collectors.toList());
        this.exerciseIds = assignment.getExercises().stream().map(Exercise::getId).collect(Collectors.toList());

        assignment.getExercises().forEach(exercise -> {
            Map<String, SubmissionEvaluation> exerciseSubmissionsByStudentEmail = new LinkedHashMap<>(students.size());
            sortedStudents.forEach(user -> exerciseSubmissionsByStudentEmail.put(user.getEmailAddress(), SubmissionEvaluation.NO_SUBMISSION));

            this.byExercises.put(exercise.getId(), exerciseSubmissionsByStudentEmail);
        });

        sortedStudents.forEach(student -> {
            Map<String, SubmissionEvaluation> exerciseSubmissionsByExerciseId = new LinkedHashMap<>(assignment.getExercises().size());

            assignment.getExercises().forEach(exercise -> exerciseSubmissionsByExerciseId.put(exercise.getId(), SubmissionEvaluation.NO_SUBMISSION));
            this.byStudents.put(student.getEmailAddress(), exerciseSubmissionsByExerciseId);
        });

        for (var entry : assignmentSubmissionsByUser.entrySet()) {
            var user = entry.getKey();
            var submissions = entry.getValue();
            double totalScore = 0.0;
            for (var submission : submissions) {
                var exerciseId = submission.getExerciseId();
                this.byExercises.get(exerciseId).put(user.getEmailAddress(), submission.getResult());

                this.byStudents.get(user.getEmailAddress()).put(exerciseId, submission.getResult());

                totalScore += submission.getScore();
            }

            totalsByStudent.put(user.getEmailAddress(), totalScore);
        }
    }
}