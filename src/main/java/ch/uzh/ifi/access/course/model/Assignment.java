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

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Assignment extends AssignmentConfig implements IndexedCollection<Exercise>, Indexed<Assignment>, HasBreadCrumbs {
    private final String id;
    private int index;

    @JsonIgnore
    @ToString.Exclude
    private Course course;

    private List<Exercise> exercises;

    public Assignment(String name) {
        this.id = new Utils().getID(name);

        this.exercises = new ArrayList<>();
    }

    @Builder
    private Assignment(String title, String description, ZonedDateTime publishDate, ZonedDateTime dueDate, String id, int index, Course course, List<Exercise> exercises) {
        super(title, description, publishDate, dueDate);
        this.id = id;
        this.index = index;
        this.course = course;
        this.exercises = exercises;
    }

    public void set(AssignmentConfig other) {
        this.title = other.getTitle();
        this.description = other.getDescription();
        this.publishDate = other.getPublishDate();
        this.dueDate = other.getDueDate();
    }

    public void update(Assignment other) {
        set(other);
        this.update(other.getIndexedItems());
    }

    public void addExercise(Exercise ex) {
        exercises.add(ex);
        ex.setAssignment(this);
        exercises.sort(Comparator.comparing(Exercise::getIndex));
    }

    public void addExercises(Exercise... exercises) {
        for (Exercise ex : exercises) {
            addExercise(ex);
        }
    }

    public Optional<Exercise> findExerciseById(String id) {
        return exercises.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public double getMaxScore() {
        return exercises.stream().mapToDouble(ExerciseConfig::getMaxScore).sum();
    }


    @Override
    public List<Exercise> getIndexedItems() {
        return exercises;
    }

    @Override
    public List<BreadCrumb> getBreadCrumbs() {
        List<BreadCrumb> bc = new ArrayList<>();
        BreadCrumb c = new BreadCrumb(this.getCourse().title, "courses/" + this.getCourse().getId());
        BreadCrumb a = new BreadCrumb(this.title, "courses/" + this.getCourse().getId() + "/assignments/" + this.id);
        bc.add(c);
        bc.add(a);

        return bc;
    }
}

