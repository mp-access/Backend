package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@ToString(exclude = "assignment")
public class Exercise extends ExerciseConfig implements Indexed<Exercise> {

    private final String id;
    private int index;
    private String gitHash;

    @JsonIgnore
    private Assignment assignment;

    private String question;

    @JsonIgnore
    private List<VirtualFile> private_files;
    @JsonIgnore
    private List<VirtualFile> solution_files;

    private List<VirtualFile> resource_files;
    private List<VirtualFile> public_files;

    public Exercise(String name) {
        super();
        this.id = new Utils().getID(name);

        System.out.println("Creating exercise with: " + name);
        System.out.println("\t -> id = " + this.id);

        this.private_files = new ArrayList<>();
        this.solution_files = new ArrayList<>();
        this.resource_files = new ArrayList<>();
        this.public_files = new ArrayList<>();
    }

    public void set(ExerciseConfig other) {
        this.type = other.getType();
        this.language = other.getLanguage();
        this.isGraded = other.getIsGraded();
        this.maxScore = other.getMaxScore();
        this.maxSubmits = other.getMaxSubmits();
        this.options = other.getOptions();
        this.solutions = other.getSolutions();
    }

    public void update(Exercise other) {
        set(other);
        this.gitHash = other.gitHash;
        this.private_files = other.private_files;
        this.public_files = other.public_files;
        this.solution_files = other.solution_files;
        this.resource_files = other.resource_files;
        this.question = other.question;
    }

    public Optional<VirtualFile> getFileById(String id) {
        //TODO: Also search in private files
        Stream<VirtualFile> files = Stream.concat(Stream.concat(public_files.stream(), resource_files.stream()), solution_files.stream());
        return files.filter(file -> file.getId().equals(id)).findFirst();
    }

    public String getAssignmentId() {
        return assignment.getId();
    }

    public String getCourseId() {
        return assignment.getCourse().getId();
    }

    public boolean isPastDueDate() {
        return assignment.isPastDueDate();
    }

    @JsonIgnore
    public String getTextSolution() {
        if (ExerciseType.text.equals(this.getType()) && this.getSolutions() != null && !this.getSolutions().isEmpty()) {
            String solution = this.getSolutions().get(0);
            return StringUtils.isEmpty(solution) ? "" : solution.trim();
        }

        throw new UnsupportedOperationException("Calling getTextSolution on non-text type exercise");
    }

    public boolean hasBreakingChange(Exercise other) {
        return !(
                Objects.equals(this.getType(), other.getType()) &&
                Objects.equals(this.getLanguage(), other.getLanguage()) &&
                Objects.equals(this.getSolutions(), other.getSolutions()) &&
                Objects.equals(this.question, other.question) &&
                Objects.equals(this.private_files, other.private_files) &&
                Objects.equals(this.resource_files, other.resource_files) &&
                Objects.equals(this.public_files, other.public_files)
        );
    }
}
