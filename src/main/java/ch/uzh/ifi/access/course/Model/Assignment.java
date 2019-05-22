package ch.uzh.ifi.access.course.Model;

import lombok.Data;

import java.util.*;

@Data
public class Assignment {
    private final UUID id;

    private String title;
    private String description;
    private Date publishDate;
    private Date dueDate;

    private List<Exercise> exercises = new ArrayList<>();

    public Assignment() {
        this.id = UUID.randomUUID();
    }

    public void set(Assignment other) {
        this.title = other.title;
        this.description = other.description;
        this.publishDate = other.publishDate;
        this.dueDate = other.dueDate;
    }

    public Optional<Exercise> findExerciseById(UUID id) {
        return exercises.stream().filter(e -> e.getId().equals(id)).findFirst();
    }
}

