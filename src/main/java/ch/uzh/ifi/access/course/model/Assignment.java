package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
public class Assignment implements IndexedCollection<Exercise>, Indexed<Assignment> {
    private final String id;
    private int index;

    @JsonIgnore
    private Course course;

    private String title;
    private String description;
    private LocalDateTime publishDate;
    private LocalDateTime dueDate;

    private List<Exercise> exercises;

    public Assignment() {
        this.id = new Utils().getID();
        this.exercises = new ArrayList<>();
    }

    public void set(Assignment other) {
        this.title = other.title;
        this.description = other.description;
        this.publishDate = other.publishDate;
        this.dueDate = other.dueDate;
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
        return LocalDateTime.now().isAfter(dueDate);
    }

    @Override
    public List<Exercise> getIndexedItems() {
        return exercises;
    }
}

