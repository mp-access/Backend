package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.*;
import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExerciseWithSolutionsDTO extends ExerciseConfig {

    private final String id;
    private int index;
    private String gitHash;

    private String question;

    private List<VirtualFile> solution_files;
    private List<VirtualFile> resource_files;
    private List<VirtualFile> public_files;

    private String courseId;
    private String assignmentId;

    public ExerciseWithSolutionsDTO(Exercise exercise) {
        this.id = exercise.getId();
        this.gitHash = exercise.getGitHash();
        this.type = exercise.getType();
        this.language = exercise.getLanguage();
        this.isGraded = exercise.getIsGraded();
        this.question = exercise.getQuestion();
        this.maxSubmits = exercise.getMaxSubmits();
        this.maxScore = exercise.getMaxScore();
        this.options = exercise.getOptions();
        this.solutions = exercise.getSolutions();
        this.solution_files = exercise.getSolution_files();
        this.resource_files = exercise.getResource_files();
        this.public_files = exercise.getPublic_files();
        this.executionLimits = exercise.getExecutionLimits();
        this.courseId = exercise.getCourseId();
        this.assignmentId = exercise.getAssignmentId();
    }
}
