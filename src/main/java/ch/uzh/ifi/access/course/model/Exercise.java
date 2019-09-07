package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
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

        this.private_files = new ArrayList<>();
        this.solution_files = new ArrayList<>();
        this.resource_files = new ArrayList<>();
        this.public_files = new ArrayList<>();
    }

    @Builder
    private Exercise(ExerciseType type, String language, Boolean isGraded, int maxScore, int maxSubmits, List<String> options, List<String> solutions, List<String> hints, String id, int index, String gitHash, Assignment assignment, String question, List<VirtualFile> private_files, List<VirtualFile> solution_files, List<VirtualFile> resource_files, List<VirtualFile> public_files) {
        super(type, language, isGraded, maxScore, maxSubmits, options, solutions, hints);
        this.id = id;
        this.index = index;
        this.gitHash = gitHash;
        this.assignment = assignment;
        this.question = question;
        this.private_files = private_files;
        this.solution_files = solution_files;
        this.resource_files = resource_files;
        this.public_files = public_files;
    }

    public void set(ExerciseConfig other) {
        this.type = other.getType();
        this.language = other.getLanguage();
        this.isGraded = other.getIsGraded();
        this.maxScore = other.getMaxScore();
        this.maxSubmits = other.getMaxSubmits();
        this.options = other.getOptions();
        this.solutions = other.getSolutions();
        this.hints = other.getHints();
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

    /**
     * For privileged users or if solutions have been published, search on all folders
     *
     * @param id file id
     */
    // TODO: should private files ever be visible to a student? (for example after the due date)?
    public Optional<VirtualFile> getAnyFileById(String id) {
        Stream<VirtualFile> files = Stream.concat(Stream.concat(public_files.stream(), resource_files.stream()), solution_files.stream());
        return files.filter(file -> file.getId().equals(id)).findFirst();
    }

    /**
     * For normal users or if solutions have not been published yet, we should only search in files in the public and resource folders
     *
     * @param id file id
     */
    public Optional<VirtualFile> getPublicOrResourcesFile(String id) {
        Stream<VirtualFile> files = Stream.concat(public_files.stream(), resource_files.stream());
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

    @JsonIgnore
    public Set<Integer> getMultipleChoiceSolution() {
        if (ExerciseType.multipleChoice.equals(type)
                && options != null && !options.isEmpty()
                && solutions != null && !solutions.isEmpty()) {

            return options.stream().filter(o -> solutions.contains(o)).map(o -> options.indexOf(o)).collect(Collectors.toSet());
        }

        throw new UnsupportedOperationException("Calling getMultipleChoiceSolution on non-multipleChoice type exercise");
    }

    @JsonIgnore
    public Integer getSingleChoiceSolution() {
        if (ExerciseType.singleChoice.equals(type)
                && options != null && !options.isEmpty()
                && solutions != null && solutions.size() == 1
        ) {
            return options.stream().filter(o -> solutions.contains(o)).map(o -> options.indexOf(o)).findFirst().orElseThrow();
        }

        throw new UnsupportedOperationException("Calling getMultipleChoiceSolution on non-multipleChoice type exercise");
    }


    public boolean isBreakingChange(Exercise other) {
        return !(Objects.equals(this.gitHash, other.gitHash) &&
                Objects.equals(this.index, other.index) &&
                Objects.equals(this.type, other.type) &&
                Objects.equals(this.language, other.language) &&
                Objects.equals(this.question, other.question) &&
                Objects.equals(this.private_files, other.private_files) &&
                Objects.equals(this.resource_files, other.resource_files) &&
                Objects.equals(this.public_files, other.public_files)
        );
    }
}
