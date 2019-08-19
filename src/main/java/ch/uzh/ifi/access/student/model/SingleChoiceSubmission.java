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
@TypeAlias("singleChoice")
public class SingleChoiceSubmission extends StudentSubmission {

    private Integer choice;

    @Builder
    public SingleChoiceSubmission(String id, int version, String userId, String commitId, String exerciseId, Instant timestamp, Integer choice, boolean isInvalid) {
        super(id, version, userId, commitId, exerciseId, timestamp, null, isInvalid);
        this.choice = choice;
    }
}
