package ch.uzh.ifi.access.student.model;

import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
@TypeAlias("text")
public class TextSubmission extends StudentSubmission {

    private String answer;

    @Builder
    public TextSubmission(String id, int version, String userId, String commitId, String exerciseId, boolean graded, Instant timestamp, String answer) {
        super(id, version, userId, commitId, exerciseId, graded, timestamp, null);
        this.answer = answer;
    }
}
