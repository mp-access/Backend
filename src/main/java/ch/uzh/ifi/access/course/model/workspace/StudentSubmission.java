package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.course.model.Exercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

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

    private String exerciseId;

    @Transient
    private Exercise exercise;

    private Instant timestamp;

    private SubmissionEvaluation result;

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        this.exerciseId = exercise.getId();
    }
}
