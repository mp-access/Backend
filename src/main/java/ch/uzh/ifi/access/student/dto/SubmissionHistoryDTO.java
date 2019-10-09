package ch.uzh.ifi.access.student.dto;

import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class SubmissionHistoryDTO {

    private final List<SubmissionMetadata> submissions;
    private final List<SubmissionMetadata> runs;

    private final SubmissionCount submissionCount;

    private final boolean isPastDueDate;

    private final LocalDateTime dueDate;

    public SubmissionHistoryDTO(List<StudentSubmission> submissions, List<StudentSubmission> runs, SubmissionCount submissionCount, LocalDateTime dueDate, boolean isPastDueDate) {
        this.submissions = submissions.stream().map(SubmissionMetadata::new).collect(Collectors.toList());
        this.runs = runs.stream().map(SubmissionMetadata::new).collect(Collectors.toList());
        this.submissionCount = submissionCount;
        this.isPastDueDate = isPastDueDate;
        this.dueDate = dueDate;
    }

    @Value
    public static class SubmissionMetadata {
        private final String id;

        private final int version;

        private final Instant timestamp;

        private final String commitHash;

        private final boolean graded;

        private final boolean isInvalid;

        private final boolean isTriggeredReSubmission;

        private SubmissionEvaluation result;

        SubmissionMetadata(StudentSubmission submission) {
            this.id = submission.getId();
            this.version = submission.getVersion();
            this.timestamp = submission.getTimestamp();
            this.commitHash = submission.getCommitId();
            this.result = submission.getResult();
            this.isInvalid = submission.isInvalid();
            this.isTriggeredReSubmission = submission.isTriggeredReSubmission();

            if (submission instanceof CodeSubmission) {
                this.graded = submission.isGraded();
            } else {
                this.graded = true;
            }
        }
    }
}
