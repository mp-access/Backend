package ch.uzh.ifi.access.course.model.workspace;

import lombok.Value;

import java.time.Instant;

@Value
public class SubmissionEvaluation {

    private int score;

    private Instant timestamp;
}
