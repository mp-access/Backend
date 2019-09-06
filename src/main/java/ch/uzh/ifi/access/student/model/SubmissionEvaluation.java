package ch.uzh.ifi.access.student.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@Value
@Data
@Builder
public class SubmissionEvaluation {

    public static SubmissionEvaluation NO_SUBMISSION = new SubmissionEvaluation(new Points(0, 0), 0, Instant.MIN, Collections.emptyList());

    private Points points;

    private int maxScore;

    private Instant timestamp;

    private List<String> hints;

    @JsonProperty
    public boolean hasSubmitted() {
        return !NO_SUBMISSION.equals(this);
    }

    public double getScore() {
        return Math.round((points.getCorrect() / (double) points.getMax() * maxScore) * 4) / 4d;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Points {
        private int correct;
        private int max;
    }

}
