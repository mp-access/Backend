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

    List<SubmissionMetadata> submissions;
    List<SubmissionMetadata> runs;

    SubmissionCount submissionCount;

    public SubmissionHistoryDTO(List<StudentSubmission> submissions, List<StudentSubmission> runs, SubmissionCount submissionCount) {
        this.submissions = submissions.stream().map(SubmissionMetadata::new).collect(Collectors.toList());
        this.runs = runs.stream().map(SubmissionMetadata::new).collect(Collectors.toList());
        this.submissionCount = submissionCount;
    }

    @Value
    public static class SubmissionMetadata {
        String id;

        int version;

        Instant timestamp;

        String commitHash;

        boolean graded;

        boolean isInvalid;

        boolean isTriggeredReSubmission;

        SubmissionEvaluation result;

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
