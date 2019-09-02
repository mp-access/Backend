package ch.uzh.ifi.access.student.dto;

import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class SubmissionHistoryDTO {

    private final List<SubmissionMetadata> submissions;

    private final SubmissionCount submissionCount;

    public SubmissionHistoryDTO(List<StudentSubmission> submissions, SubmissionCount submissionCount) {
        this.submissions = submissions.stream().map(SubmissionMetadata::new).collect(Collectors.toList());
        this.submissionCount = submissionCount;
    }

    @Value
    public static class SubmissionMetadata {
        private final String id;

        private final int version;

        private final Instant timestamp;

        private final String commitHash;

        private final boolean graded;

        private final boolean isInvalid;

        private SubmissionEvaluation result;

        SubmissionMetadata(StudentSubmission submission) {
            this.id = submission.getId();
            this.version = submission.getVersion();
            this.timestamp = submission.getTimestamp();
            this.commitHash = submission.getCommitId();
            this.result = submission.getResult();
            this.isInvalid = submission.isInvalid();

            if (submission instanceof CodeSubmission) {
                this.graded = ((CodeSubmission) submission).isGraded();
            } else {
                this.graded = true;
            }
        }
    }
}
