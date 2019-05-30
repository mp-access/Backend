package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
public abstract class StudentSubmission {

    @Id
    private String id;

    private int version;

    private String userId;

    private String commitId;

    private String courseId;

    private String assignmentId;

    private String exerciseId;

    @Transient
    private Exercise exercise;

    private LocalDateTime timestamp;

    private AnswerEvaluation result;

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
