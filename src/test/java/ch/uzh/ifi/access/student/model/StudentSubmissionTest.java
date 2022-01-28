package ch.uzh.ifi.access.student.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StudentSubmissionTest {

    @Test
    public void userIdMatchesBothNull() {
        TextSubmission submission = new TextSubmission();
        Assertions.assertFalse(submission.userIdMatches(null));
    }

    @Test
    public void userIdMatchesSubmissionNull() {
        TextSubmission submission = new TextSubmission();
        Assertions.assertFalse(submission.userIdMatches("asdf"));
    }

    @Test
    public void userIdMatchesDifferentIds() {
        TextSubmission submission = new TextSubmission();
        submission.setUserId("123");
        Assertions.assertFalse(submission.userIdMatches("zzz"));
    }

    @Test
    public void userIdMatchesTrue() {
        TextSubmission submission = new TextSubmission();
        submission.setUserId("123");
        Assertions.assertTrue(submission.userIdMatches(submission.getUserId()));
    }
}