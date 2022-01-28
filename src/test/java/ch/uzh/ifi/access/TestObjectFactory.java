package ch.uzh.ifi.access;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.model.*;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

public class TestObjectFactory {

    public static Assignment createAssignmentWithExercises(boolean isPublished, boolean isDue) {
        Assignment assignment = new Assignment(UUID.randomUUID().toString());
        assignment.setIndex(0);
        assignment.setDueDate(ZonedDateTime.now().plusDays(isDue ? -1 : 7));
        assignment.setPublishDate(ZonedDateTime.now().plusDays(isPublished ? 0 : 1));
        List.of(createCodeExercise(), createTextExercise(), createMultipleChoiceExercise())
                .forEach(exercise -> {
                    assignment.addExercise(exercise);
                    exercise.setAssignment(assignment);
                });
        return assignment;
    }

    public static Course createCourseWithAssignments(String title, List<Assignment> assignments) {
        Course course = new Course(title);
        assignments.forEach(course::addAssignment);
        return course;
    }

    public static Course createCourseWithAssignmentAndExercises(String title) {
        return createCourseWithAssignments(title, List.of(createAssignmentWithExercises(true, false)));
    }

    public static Exercise createCodeExercise(String id) {
        Exercise exercise = new Exercise(id);
        exercise.setQuestion("Code question");
        exercise.setLanguage("python");
        exercise.setType(ExerciseType.code);
        exercise.setMaxScore(10);
        exercise.setMaxSubmits(10);
        exercise.setPublic_files(List.of(
                createVirtualFile("file-1", "py", false),
                createVirtualFile("file-2", "py", false)));
        return exercise;
    }

    public static Exercise createCodeExercise() {
        return createCodeExercise(UUID.randomUUID().toString());
    }

    public static Exercise createTextExercise() {
        Exercise exercise = new Exercise(UUID.randomUUID().toString());
        exercise.setQuestion("Text question");
        exercise.setType(ExerciseType.text);
        exercise.setGitHash("0x123456");
        exercise.setMaxScore(1);
        return exercise;
    }

    public static Exercise createMultipleChoiceExercise() {
        Exercise exercise = new Exercise(UUID.randomUUID().toString());
        exercise.setQuestion("MC question");
        exercise.setType(ExerciseType.multipleChoice);
        exercise.setGitHash("0x123456");
        exercise.setMaxScore(3);
        return exercise;
    }

    public static VirtualFile createVirtualFile(String name, String extension, boolean isMediaType) {
        String virtualPath = String.format("folder/%s.%s", name, extension);
        VirtualFile virtualFile = new VirtualFile("/root/" + virtualPath, virtualPath);
        virtualFile.setName(name);
        virtualFile.setExtension(extension);
        virtualFile.setIsMediaType(isMediaType);
        virtualFile.setContent("print(\"Hello, world!\")\na = 1 + 1");
        return virtualFile;
    }

    public static CodeSubmission createCodeAnswer(String id, String userId, String exerciseId, int version) {
        return CodeSubmission.builder()
                .id(id)
                .userId(userId)
                .exerciseId(exerciseId)
                .version(version)
                .commitId("commit-1")
                .publicFiles(List.of(
                        createVirtualFile("file-1", "py", false),
                        createVirtualFile("file-2", "py", false)))
                .isGraded(true)
                .build();
    }

    public static CodeSubmission createCodeAnswer(String userId, String exerciseId, int version) {
        return createCodeAnswer(UUID.randomUUID().toString(), userId, exerciseId, version);
    }

    public static CodeSubmission createCodeAnswer(String userId, String exerciseId) {
        return createCodeAnswer(UUID.randomUUID().toString(), userId, exerciseId, 0);
    }

    public static TextSubmission createTextAnswer(String userId, String exerciseId) {
        return TextSubmission.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .exerciseId(exerciseId)
                .version(0)
                .commitId("commit-1")
                .timestamp(Instant.now())
                .answer("Hello world!")
                .build();
    }

    public static MultipleChoiceSubmission createMultipleChoiceAnswer(String userId, String exerciseId) {
        return MultipleChoiceSubmission.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .exerciseId(exerciseId)
                .version(0)
                .commitId("commit-1")
                .timestamp(Instant.now())
                .choices(Set.of(1, 3))
                .build();
    }

    public static CourseAuthentication createCourseAuthentication(Set<GrantedCourseAccess> grantedCourseAccesses) {
        OAuth2Request request = new OAuth2Request(Map.of(),
                "client",
                List.of(), true,
                Set.of("openid"),
                Set.of(), null, null, null);
        return new CourseAuthentication(request, null, grantedCourseAccesses, "");
    }
}
