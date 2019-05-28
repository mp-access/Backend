package ch.uzh.ifi.access;

import ch.uzh.ifi.access.course.model.*;
import ch.uzh.ifi.access.course.model.workspace.CodeAnswer;
import ch.uzh.ifi.access.course.model.workspace.MultipleChoiceAnswer;
import ch.uzh.ifi.access.course.model.workspace.TextAnswer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TestObjectFactory {

    public static Course createCourse(String title) {
        Course course = new Course();
        course.setTitle(title);
        return course;
    }

    public static Assignment createAssignment(String title) {
        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription("Some description");
        assignment.setDueDate(LocalDateTime.now().plusDays(7));
        assignment.setPublishDate(LocalDateTime.now());
        return assignment;
    }

    public static Exercise createCodeExercise(String question) {
        Exercise exercise = new Exercise();
        exercise.setLanguage("python");
        exercise.setQuestion(question);
        exercise.setType(ExerciseType.code);
        return exercise;
    }

    public static Exercise createTextExercise(String question) {
        Exercise exercise = new Exercise();
        exercise.setQuestion(question);
        exercise.setType(ExerciseType.text);
        return exercise;
    }

    public static Exercise createMultipleChoiceExercise(String question) {
        Exercise exercise = new Exercise();
        exercise.setQuestion(question);
        exercise.setType(ExerciseType.multipleChoice);
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

    public static CodeAnswer createCodeAnswer() {
        return CodeAnswer.builder()
                .version(1)
                .userId("user-1")
                .commitId("commit-1")
                .courseId("course-1")
                .assignmentId("assignment-1")
                .exerciseId("exercise-1")
                .timestamp(LocalDateTime.now())
                .publicFiles(List.of(createVirtualFile("test", "py", false), createVirtualFile("test2", "py", false)))
                .isOfficialSubmission(true)
                .build();
    }

    public static TextAnswer createTextAnswer() {
        return TextAnswer.builder()
                .version(1)
                .userId("user-1")
                .commitId("commit-1")
                .courseId("course-1")
                .assignmentId("assignment-1")
                .exerciseId("exercise-1")
                .timestamp(LocalDateTime.now())
                .answer("Hello world!")
                .build();
    }

    public static MultipleChoiceAnswer createMultipleChoiceAnswer() {
        return MultipleChoiceAnswer.builder()
                .version(1)
                .userId("user-1")
                .commitId("commit-1")
                .courseId("course-1")
                .assignmentId("assignment-1")
                .exerciseId("exercise-1")
                .timestamp(LocalDateTime.now())
                .choices(Set.of(1, 3))
                .build();
    }
}
