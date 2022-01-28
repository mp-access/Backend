package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Course extends CourseConfig implements OrderedCollection<Assignment>, HasBreadCrumbs {
    private final String id;

    @ToString.Exclude
    private String gitHash;
    @ToString.Exclude
    private String gitURL;
    private String directory;


    /**
     * A role name is composed by combining the course title and the semester (if available), all in lowercase and
     * replacing spaces with dashes. The value is initialised and cached only after the getter is first called.
     */
    @Getter(lazy = true)
    private final String roleName = parseRoleName();

    public String parseRoleName() {
        return Joiner.on("-").skipNulls().join(title, semester).toLowerCase().replace(" ", "-");
    }

    private List<Assignment> assignments;

    public Course(String name) {
        this.id = new Utils().getID(name);
        this.title = name;
        this.assignments = new ArrayList<>();
    }

    @Builder
    public Course(String title, String description, String owner, String semester, ZonedDateTime startDate, ZonedDateTime endDate, List<String> admins, List<String> assistants, List<String> students, String id, String gitHash, String gitURL, String directory, List<Assignment> assignments) {
        super(title, description, owner, semester, startDate, endDate, admins, assistants, students);
        this.id = id;
        this.gitHash = gitHash;
        this.gitURL = gitURL;
        this.directory = directory;
        this.assignments = assignments;
    }


    public void set(CourseConfig other) {
        this.title = other.getTitle();
        this.description = other.getDescription();
        this.owner = other.getOwner();
        this.semester = other.getSemester();
        this.startDate = other.getStartDate();
        this.endDate = other.getEndDate();
        this.admins = other.getAdmins();
        this.assistants = other.getAssistants();
        this.students = other.getStudents();
    }

    public void update(Course other) {
        set(other);
        this.gitHash = other.gitHash;

        this.update(other.getOrderedItems());
    }

    public void addAssignment(Assignment a) {
        a.setCourse(this);
        assignments.add(a);
        assignments.sort(Comparator.comparing(Assignment::getOrder));
        indexAssignments();
    }

    public void addAssignments(Assignment... assignments) {
        for (Assignment a : assignments) {
            addAssignment(a);
        }
    }

    public Optional<Assignment> getAssignmentById(String id) {
        return assignments.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    @JsonIgnore
    @Override
    public List<Assignment> getOrderedItems() {
        return assignments;
    }

    @JsonIgnore
    public List<Exercise> getExercises() {
        return assignments.stream().flatMap(assignment -> assignment.getExercises().stream()).collect(Collectors.toList());
    }

    @Override
    public List<BreadCrumb> getBreadCrumbs() {
        BreadCrumb course = new BreadCrumb(this.title, "courses/" + this.roleName);

        return List.of(course);
    }

    private void indexAssignments() {
        for (var i = 0; i < assignments.size(); i++) {
            var assignment = assignments.get(i);
            assignment.setIndex(i + 1);
        }
    }

    public boolean hasParticipant(String emailAddress) {
        return students.contains(emailAddress) || assistants.contains(emailAddress) || admins.contains(emailAddress);
    }
}