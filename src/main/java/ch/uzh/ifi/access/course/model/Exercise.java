package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@AllArgsConstructor
@ToString(exclude = "assignment")
public class Exercise implements Indexed<Exercise> {

    private final String id;
    private int index;

    @JsonIgnore
    private Assignment assignment;

    private String gitHash;
    private ExerciseType type;
    private String language;
    private Boolean isGraded;

    private String question;
    private int maxSubmits;

    private List<String> options;
    private List<String> solutions;

    private int maxScore;

    @JsonIgnore
    private List<VirtualFile> private_files;
    @JsonIgnore
    private List<VirtualFile> solution_files;

    private List<VirtualFile> resource_files;
    private List<VirtualFile> public_files;

    public Exercise() {
        this.id = new Utils().getID();
        this.isGraded = true;
        this.maxSubmits = 1;
        this.options = new ArrayList<>();
        this.solutions = new ArrayList<>();
        this.private_files = new ArrayList<>();
        this.solution_files = new ArrayList<>();
        this.resource_files = new ArrayList<>();
        this.public_files = new ArrayList<>();
    }

    public void set(Exercise other) {
        this.type = other.type;
        this.language = other.language;
        this.maxSubmits = other.maxSubmits;
        this.solutions = other.solutions;
        this.options = other.options;
        this.isGraded = other.isGraded;
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
        if (ExerciseType.text.equals(type) && solutions != null && !solutions.isEmpty()) {
            String solution = solutions.get(0);
            return StringUtils.isEmpty(solution) ? "" : solution.trim();
        }

        throw new UnsupportedOperationException("Calling getTextSolution on non-text type exercise");
    }

    @JsonIgnore
    public Set<Integer> getMultipleChoiceSolution() {
        if (ExerciseType.multipleChoice.equals(type)
                && options != null && !options.isEmpty()
                && solutions != null && !solutions.isEmpty()) {

            return options.stream().filter(o -> solutions.contains(o)).map(o -> options.indexOf(o)).collect(Collectors.toSet());
        }

        throw new UnsupportedOperationException("Calling getMultipleChoiceSolution on non-multipleChoice type exercise");
    }

    public boolean hasChanged(Exercise other) {
        return !(Objects.equals(this.index, other.index) &&
                Objects.equals(this.type, other.type) &&
                Objects.equals(this.language, other.language) &&
                Objects.equals(this.question, other.question) &&
                Objects.equals(this.maxSubmits, other.maxSubmits) &&
                Objects.equals(this.private_files, other.private_files) &&
                Objects.equals(this.solution_files, other.solution_files) &&
                Objects.equals(this.resource_files, other.resource_files) &&
                Objects.equals(this.public_files, other.public_files) &&
                Objects.equals(this.solutions, other.solutions) &&
                Objects.equals(this.isGraded, other.isGraded)
        );
    }
}
