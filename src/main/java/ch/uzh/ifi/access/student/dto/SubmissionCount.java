package ch.uzh.ifi.access.student.dto;

import lombok.Value;

@Value
public class SubmissionCount {
    /**
     * Max submissions for exercise
     */
    private int maxSubmissions;

    /**
     * Valid submissions already submitted
     */
    private int validSubmissionCount;

    /**
     * Available submissions remaining
     *
     * @return available submissions remaining
     */
    public int getSubmissionsRemaining() {
        return maxSubmissions - validSubmissionCount;
    }
}