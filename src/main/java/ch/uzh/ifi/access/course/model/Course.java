package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Course extends CourseConfig implements IndexedCollection<Assignment>, HasBreadCrumbs {
    private final String id;

    @ToString.Exclude
    private String gitHash;
    @ToString.Exclude
    private String gitURL;
    private String directory;

    private List<Assignment> assignments;

    public Course(String name) {
        this.id = new Utils().getID(name);

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

        this.update(other.getIndexedItems());
    }

    public void addAssignment(Assignment a) {
        a.setCourse(this);
        assignments.add(a);
        assignments.sort(Comparator.comparing(Assignment::getIndex));
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
    public List<Assignment> getIndexedItems() {
        return assignments;
    }

    @JsonIgnore
    public List<Exercise> getExercises() {
        return assignments.stream().flatMap(assignment -> assignment.getExercises().stream()).collect(Collectors.toList());
    }

    @Override
    public List<BreadCrumb> getBreadCrumbs() {
        List<BreadCrumb> bc = new ArrayList<>();
        BreadCrumb c = new BreadCrumb(this.title, "courses/" + this.id);
        bc.add(c);

        return bc;
    }

    public boolean hasParticipant(String emailAddress) {
        return students.contains(emailAddress) || assistants.contains(emailAddress) || admins.contains(emailAddress);
    }
}