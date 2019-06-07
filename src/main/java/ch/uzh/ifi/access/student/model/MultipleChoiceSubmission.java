package ch.uzh.ifi.access.student.model;

import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "studentSubmissions")
@TypeAlias("multipleChoice")
public class MultipleChoiceSubmission extends StudentSubmission {

    private Set<Integer> choices;

    @Builder
    public MultipleChoiceSubmission(String id, int version, String userId, String commitId, String exerciseId, Instant timestamp, Set<Integer> choices) {
        super(id, version, userId, commitId, exerciseId, timestamp, null);
        this.choices = choices;
    }
}
