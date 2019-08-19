package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
public class Assignment extends AssignmentConfig implements IndexedCollection<Exercise>, Indexed<Assignment> {
    private final String id;
    private int index;

    @JsonIgnore
    private Course course;

    private List<Exercise> exercises;

    public Assignment(String name) {
        this.id = new Utils().getID(name);

        this.exercises = new ArrayList<>();
    }

    @Builder
    private Assignment(String title, String description, LocalDateTime publishDate, LocalDateTime dueDate, String id, int index, Course course, List<Exercise> exercises) {
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

    public boolean isPastDueDate() {
        return LocalDateTime.now().isAfter(this.getDueDate());
    }

    public int getMaxScore() {
        return exercises.stream().mapToInt(e -> e.getMaxScore()).sum();
    }


    @Override
    public List<Exercise> getIndexedItems() {
        return exercises;
    }
}

