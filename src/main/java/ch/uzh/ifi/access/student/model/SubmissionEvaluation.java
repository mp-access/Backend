package ch.uzh.ifi.access.student.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@SuppressWarnings("unused")
@Value
@Data
@Builder
public class SubmissionEvaluation {

    public static SubmissionEvaluation NO_SUBMISSION = new SubmissionEvaluation(new Points(0, 0), 0, Instant.MIN);

    private Points points;

    private int maxScore;

    private Instant timestamp;

    @JsonProperty
    public boolean hasSubmitted() {
        return !NO_SUBMISSION.equals(this);
    }

    public int getScore() {
        return points.getCorrect() / points.getMax() * maxScore;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Points {
        private int correct;
        private int max;
    }

}
