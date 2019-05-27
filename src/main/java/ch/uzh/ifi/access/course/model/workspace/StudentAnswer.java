package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class StudentAnswer {

    private String id;

    private int version;

    private String userId;

    private String commitId;

    private String courseId;

    private String assignmentId;

    private String exerciseId;

    private Exercise exercise;

    private LocalDateTime timestamp;

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        this.exerciseId = exercise.getId();
    }

    public List<VirtualFile> getPrivateFiles() {
        return exercise.getPrivate_files();
    }

    public List<VirtualFile> getResourceFiles() {
        return exercise.getResource_files();
    }
}
