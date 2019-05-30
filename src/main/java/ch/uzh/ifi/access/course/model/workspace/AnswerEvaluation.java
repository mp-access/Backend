package ch.uzh.ifi.access.course.model.workspace;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class AnswerEvaluation {

    private int score;

    private LocalDateTime timestamp;
}
