package ch.uzh.ifi.access.student.dto;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

public class AssignmentResultsTest {

    @Test
    public void getStudentScoreGradedSubmissionsNullOrEmpty() {
        AssignmentResults results = new AssignmentResults();
        Assertions.assertTrue(Math.abs(results.getStudentScore() - 0.0) <= 0.01);

        results.setGradedSubmissions(List.of());
        Assertions.assertTrue(Math.abs(results.getStudentScore() - 0.0) <= 0.01);
    }

    @Test
    public void getStudentScoreGradedSubmissionsSubmissionsHaveNoResults() {
        CodeSubmission submission1 = TestObjectFactory.createCodeAnswer("", "");
        CodeSubmission submission2 = TestObjectFactory.createCodeAnswer("", "");

        AssignmentResults results = new AssignmentResults();
        results.setGradedSubmissions(List.of(submission1, submission2));

        Assertions.assertTrue(Math.abs(results.getStudentScore() - 0.0) <= 0.01);
    }

    @Test
    public void getStudentScoreGradedSubmissionsSubmissionsHaveResults() {
        CodeSubmission submission1 = TestObjectFactory.createCodeAnswer("", "");
        CodeSubmission submission2 = TestObjectFactory.createCodeAnswer("", "");

        AssignmentResults results = new AssignmentResults();
        submission1.setResult(
                SubmissionEvaluation.builder()
                        .maxScore(10)
                        .points(new SubmissionEvaluation.Points(1, 2))
                        .timestamp(Instant.now())
                        .build());

        submission2.setResult(
                SubmissionEvaluation.builder()
                        .maxScore(10)
                        .points(new SubmissionEvaluation.Points(2, 2))
                        .timestamp(Instant.now())
                        .build());
        results.setGradedSubmissions(List.of(submission1, submission2));

        Assertions.assertTrue(Math.abs(results.getStudentScore() - 15.0) <= 0.01);
    }

    @Test
    public void getStudentScoreGradedSubmissionsSomeResultsNull() {
        CodeSubmission submission1 = TestObjectFactory.createCodeAnswer("", "");
        CodeSubmission submission2 = TestObjectFactory.createCodeAnswer("", "");

        AssignmentResults results = new AssignmentResults();
        submission1.setResult(
                SubmissionEvaluation.builder()
                        .maxScore(10)
                        .points(new SubmissionEvaluation.Points(1, 2))
                        .timestamp(Instant.now())
                        .build());
        results.setGradedSubmissions(List.of(submission1, submission2));

        Assertions.assertTrue(Math.abs(results.getStudentScore() - 5.0) <= 0.01);
    }
}