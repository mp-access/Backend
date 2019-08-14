package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExerciseWithSolutionsDTO {
    private final String id;

    private String assignmentId;
    private String courseId;

    private String gitHash;
    private ExerciseType type;
    private String language;

    private String question;
    private int maxSubmits;

    private List<String> options = new ArrayList<>();
    private List<String> solutions = new ArrayList<>();

    private List<VirtualFile> solution_files = new ArrayList<>();

    private List<VirtualFile> resource_files = new ArrayList<>();
    private List<VirtualFile> public_files = new ArrayList<>();

    public ExerciseWithSolutionsDTO(Exercise exercise) {
        this.id = exercise.getId();
        this.assignmentId = exercise.getAssignmentId();
        this.courseId = exercise.getCourseId();
        this.gitHash = exercise.getGitHash();
        this.type = exercise.getType();
        this.language = exercise.getLanguage();
        this.question = exercise.getQuestion();
        this.maxSubmits = exercise.getMaxSubmits();
        this.options = exercise.getOptions();
        this.solutions = exercise.getSolutions();
        this.solution_files = exercise.getSolution_files();
        this.resource_files = exercise.getResource_files();
        this.public_files = exercise.getPublic_files();
    }

    public ExerciseWithSolutionsDTO() {
        this.id = new Utils().getID();
    }
}
