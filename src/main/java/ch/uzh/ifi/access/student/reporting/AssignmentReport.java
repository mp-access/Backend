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

    private final List<String> exerciseLabel;

    private final List<String> students;

    private final List<String> usersNotFound;

    public AssignmentReport(Assignment assignment, List<User> students, Map<User, List<StudentSubmission>> assignmentSubmissionsByUser, List<String> usersNotFound) {
        this.assignmentId = assignment.getId();
        this.byExercises = new LinkedHashMap<>(assignment.getExercises().size());
        this.byStudents = new LinkedHashMap<>(students.size());
        this.totalsByStudent = new LinkedHashMap<>(students.size());
        this.usersNotFound = usersNotFound;

        Map<String, Exercise> exerciseIdToExercise = assignment.getExercises().stream().collect(Collectors.toMap(Exercise::getId, e -> e));

        // Sort lexicographically to ensure students always occur in the same order in the report
        List<User> sortedStudents = students.stream().sorted().collect(Collectors.toList());
        this.students = sortedStudents.stream().map(User::getEmailAddress).collect(Collectors.toList());
        this.exerciseLabel = assignment.getExercises().stream().map(Exercise::getAssignmentExerciseIndexing).collect(Collectors.toList());

        assignment.getExercises().forEach(exercise -> {
            Map<String, SubmissionEvaluation> exerciseSubmissionsByStudentEmail = new LinkedHashMap<>(students.size());
            sortedStudents.forEach(user -> exerciseSubmissionsByStudentEmail.put(user.getEmailAddress(), SubmissionEvaluation.NO_SUBMISSION));

            this.byExercises.put(exercise.getAssignmentExerciseIndexing(), exerciseSubmissionsByStudentEmail);
        });

        sortedStudents.forEach(student -> {
            Map<String, SubmissionEvaluation> exerciseSubmissionsByExercise = new LinkedHashMap<>(assignment.getExercises().size());

            assignment.getExercises().forEach(exercise -> exerciseSubmissionsByExercise.put(exercise.getAssignmentExerciseIndexing(), SubmissionEvaluation.NO_SUBMISSION));
            this.byStudents.put(student.getEmailAddress(), exerciseSubmissionsByExercise);
        });

        for (var entry : assignmentSubmissionsByUser.entrySet()) {
            var user = entry.getKey();
            var submissions = entry.getValue();
            double totalScore = 0.0;
            for (var submission : submissions) {
                var exerciseId = submission.getExerciseId();
                Exercise exercise = exerciseIdToExercise.get(exerciseId);

                Map<String, SubmissionEvaluation> submissionsForExercise = this.byExercises.get(exercise.getAssignmentExerciseIndexing());
                submissionsForExercise.put(user.getEmailAddress(), submission.getResult());

                this.byStudents.get(user.getEmailAddress()).put(exercise.getAssignmentExerciseIndexing(), submission.getResult());

                totalScore += submission.getScore();
            }

            totalsByStudent.put(user.getEmailAddress(), totalScore);
        }
    }
}