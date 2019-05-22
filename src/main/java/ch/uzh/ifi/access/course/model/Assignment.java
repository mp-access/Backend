package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class Assignment {
    private final String id;

    private String title;
    private String description;
    private LocalDateTime publishDate;
    private LocalDateTime dueDate;

    private List<Exercise> exercises = new ArrayList<>();

    public Assignment() {
        this.id = new Utils().getID();
    }

    public void set(Assignment other) {
        this.title = other.title;
        this.description = other.description;
        this.publishDate = other.publishDate;
        this.dueDate = other.dueDate;
    }

    public void addExercise(Exercise ex){
        exercises.add(ex);
        ex.setAssignment(this);
    }

    public Optional<Exercise> findExerciseById(String id) {
        return exercises.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public boolean isPastDueDate()
    {
        return LocalDateTime.now().isAfter(dueDate);
    }
}

