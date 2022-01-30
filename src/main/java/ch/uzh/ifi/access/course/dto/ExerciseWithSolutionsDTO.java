package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.BreadCrumb;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseConfig;
import ch.uzh.ifi.access.course.model.VirtualFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExerciseWithSolutionsDTO extends ExerciseConfig {

    private final String id;
    private int index;
    private String gitHash;

    private String question;

    @EqualsAndHashCode.Exclude
    private List<BreadCrumb> breadCrumbs;

    private List<VirtualFile> solution_files;
    private List<VirtualFile> resource_files;
    private List<VirtualFile> public_files;
    private List<VirtualFile> private_files;

    private String courseId;
    private String assignmentId;

    public ExerciseWithSolutionsDTO(Exercise exercise) {
        this.id = exercise.getId();
        this.title = exercise.getTitle();
        this.longTitle = exercise.getLongTitle();
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
        this.private_files = exercise.getPrivate_files();
        this.executionLimits = exercise.getExecutionLimits();
        this.courseId = exercise.getCourseId();
        this.assignmentId = exercise.getAssignmentId();
        this.breadCrumbs = exercise.getBreadCrumbs();
        this.index = exercise.getIndex();
    }
}
