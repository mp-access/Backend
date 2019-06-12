package ch.uzh.ifi.access.student.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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

    private Instant timestamp;

    private SubmissionEvaluation result;

}
