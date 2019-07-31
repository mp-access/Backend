package ch.uzh.ifi.access.student.model;

import lombok.Value;

import java.time.Instant;

@Value
public class SubmissionEvaluation {

    private int correctPoints;

    private int maxPoints;

    private int score;

    private Instant timestamp;

}
