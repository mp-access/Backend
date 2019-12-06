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
public class Assignment extends AssignmentConfig implements OrderedCollection<Exercise>, Ordered<Assignment>, HasBreadCrumbs {
    private final String id;
    private int index;
    private int order;

    @JsonIgnore
    @ToString.Exclude
    private Course course;

    private List<Exercise> exercises;

    public Assignment(String name) {
        this.id = new Utils().getID(name);

        this.exercises = new ArrayList<>();
    }

    @Builder
    private Assignment(String title, String description, ZonedDateTime publishDate, ZonedDateTime dueDate, String id, int order, Course course, List<Exercise> exercises) {
        super(title, description, publishDate, dueDate);
        this.id = id;
        this.order = order;
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
        this.update(other.getOrderedItems());
    }

    public void addExercise(Exercise ex) {
        exercises.add(ex);
        ex.setAssignment(this);
        exercises.sort(Comparator.comparing(Exercise::getOrder));
        indexExercises();
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
    public List<Exercise> getOrderedItems() {
        return exercises;
    }

    @Override
    public List<BreadCrumb> getBreadCrumbs() {
        BreadCrumb course = new BreadCrumb(this.getCourse().title, "courses/" + this.getCourse().getId());
        BreadCrumb assignment = new BreadCrumb(this.title, "courses/" + this.getCourse().getId() + "/assignments/" + this.id, this.index);

        return List.of(course, assignment);
    }

    private void indexExercises() {
        for (var i = 0; i < exercises.size(); i++) {
            var exercise = exercises.get(i);
            exercise.setIndex(i + 1);
        }
    }
}

