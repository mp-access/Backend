package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class SubmissionHistoryDTO {

    private final List<SubmissionMetadata> submissions;

    public SubmissionHistoryDTO(List<StudentSubmission> submissions) {
        this.submissions = submissions.stream().map(SubmissionMetadata::new).collect(Collectors.toList());
    }

    @Value
    public static class SubmissionMetadata {
        private final String id;

        private final int version;

        private final Instant timestamp;

        SubmissionMetadata(StudentSubmission submission) {
            this.id = submission.getId();
            this.version = submission.getVersion();
            this.timestamp = submission.getTimestamp();
        }
    }
}
