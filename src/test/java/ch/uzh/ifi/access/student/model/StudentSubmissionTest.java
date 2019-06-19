package ch.uzh.ifi.access.student.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StudentSubmissionTest {

    @Test
    public void userIdMatchesBothNull() {
        TextSubmission submission = new TextSubmission();
        Assertions.assertThat(submission.userIdMatches(null)).isFalse();
    }

    @Test
    public void userIdMatchesSubmissionNull() {
        TextSubmission submission = new TextSubmission();
        Assertions.assertThat(submission.userIdMatches("asdf")).isFalse();
    }

    @Test
    public void userIdMatchesDifferentIds() {
        TextSubmission submission = new TextSubmission();
        submission.setUserId("123");
        Assertions.assertThat(submission.userIdMatches("zzz")).isFalse();
    }

    @Test
    public void userIdMatchesTrue() {
        TextSubmission submission = new TextSubmission();
        submission.setUserId("123");
        Assertions.assertThat(submission.userIdMatches(submission.getUserId())).isTrue();
    }
}