package ch.uzh.ifi.access;

import ch.uzh.ifi.access.course.model.*;

import java.time.LocalDateTime;

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

    public static VirtualFile createVirtualFile(String name, String extension, boolean isMediaType) {
        VirtualFile virtualFile = new VirtualFile(String.format("/foo/bar/%s.%s", name, extension), String.format("bar/%s.%s", name, extension));
        virtualFile.setName(name);
        virtualFile.setExtension(extension);
        virtualFile.setIsMediaType(isMediaType);
        return virtualFile;
    }
}
