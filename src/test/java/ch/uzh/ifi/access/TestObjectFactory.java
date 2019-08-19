package ch.uzh.ifi.access;

import ch.uzh.ifi.access.course.model.*;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.TextSubmission;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestObjectFactory {

    public static Course createCourse(String title) {
        Course course = new Course(UUID.randomUUID().toString());
        course.setTitle(title);
        return course;
    }

    public static Assignment createAssignment(String title) {
        Assignment assignment = new Assignment(UUID.randomUUID().toString());
        assignment.setTitle(title);
        assignment.setDescription("Some description");
        assignment.setDueDate(LocalDateTime.now().plusDays(7));
        assignment.setPublishDate(LocalDateTime.now());
        return assignment;
    }

    public static Exercise createCodeExercise(String question) {
        Exercise exercise = new Exercise(UUID.randomUUID().toString());
        exercise.setLanguage("python");
        exercise.setQuestion(question);
        exercise.setType(ExerciseType.code);
        exercise.setGitHash("0x123456");
        exercise.setMaxScore(10);
        return exercise;
    }

    public static Exercise createTextExercise(String question) {
        Exercise exercise = new Exercise(UUID.randomUUID().toString());
        exercise.setQuestion(question);
        exercise.setType(ExerciseType.text);
        exercise.setGitHash("0x123456");
        exercise.setMaxScore(1);
        return exercise;
    }

    public static Exercise createMultipleChoiceExercise(String question) {
        Exercise exercise = new Exercise(UUID.randomUUID().toString());
        exercise.setQuestion(question);
        exercise.setType(ExerciseType.multipleChoice);
        exercise.setGitHash("0x123456");
        exercise.setMaxScore(3);
        return exercise;
    }

    public static VirtualFile createVirtualFile(String name, String extension, boolean isMediaType) {
        VirtualFile virtualFile = new VirtualFile(String.format("/foo/bar/%s.%s", name, extension), String.format("bar/%s.%s", name, extension));
        virtualFile.setName(name);
        virtualFile.setExtension(extension);
        virtualFile.setIsMediaType(isMediaType);
        virtualFile.setContent("print(\"Hello, world!\")\na = 1 + 1");
        return virtualFile;
    }

    public static CodeSubmission createCodeAnswerWithExerciseAndUser(String exerciseId, String userId) {
        CodeSubmission answer = createCodeAnswerWithExercise(exerciseId);
        answer.setUserId(userId);
        return answer;
    }

    public static CodeSubmission createCodeAnswerWithExercise(String exerciseId) {
        CodeSubmission answer = createCodeAnswer();
        answer.setExerciseId(exerciseId);
        return answer;
    }

    public static CodeSubmission createCodeAnswer() {
        return CodeSubmission.builder()
                .version(0)
                .userId("user-1")
                .commitId("commit-1")
                .exerciseId("exercise-1")
                .timestamp(Instant.now())
                .publicFiles(List.of(createVirtualFile("test", "py", false), createVirtualFile("test2", "py", false)))
                .graded(true)
                .build();
    }

    public static TextSubmission createTextAnswerWithExercise(String exerciseId) {
        TextSubmission answer = createTextAnswer();
        answer.setExerciseId(exerciseId);
        return answer;
    }

    public static TextSubmission createTextAnswer() {
        return TextSubmission.builder()
                .version(0)
                .userId("user-1")
                .commitId("commit-1")
                .exerciseId("exercise-1")
                .timestamp(Instant.now())
                .answer("Hello world!")
                .build();
    }

    public static MultipleChoiceSubmission createMultipleChoiceAnswerWithExercise(String exerciseId) {
        MultipleChoiceSubmission multipleChoiceSubmission = createMultipleChoiceAnswer();
        multipleChoiceSubmission.setExerciseId(exerciseId);
        return multipleChoiceSubmission;
    }

    public static MultipleChoiceSubmission createMultipleChoiceAnswer() {
        return MultipleChoiceSubmission.builder()
                .version(0)
                .userId("user-1")
                .commitId("commit-1")
                .exerciseId("exercise-1")
                .timestamp(Instant.now())
                .choices(Set.of(1, 3))
                .build();
    }
}
