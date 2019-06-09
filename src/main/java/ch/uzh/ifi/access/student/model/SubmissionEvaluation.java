package ch.uzh.ifi.access.student.model;

import lombok.Value;

import java.time.Instant;

@Value
public class SubmissionEvaluation {

    private int score;

    private Instant timestamp;
}
