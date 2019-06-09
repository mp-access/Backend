package ch.uzh.ifi.access.student.dto;

import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.TextSubmission;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswerDTO {

    private ExerciseType type;

    private JsonNode details;

    public StudentSubmission createSubmission(String userId, String exerciseId, String commitHash) {
        StudentSubmission submission = createSubmission();
        submission.setExerciseId(exerciseId);
        submission.setUserId(userId);
        submission.setTimestamp(Instant.now());
        submission.setCommitId(commitHash);
        return submission;
    }

    public StudentSubmission createSubmission() {
        ObjectMapper mapper = new ObjectMapper();
        switch (type) {
            case code:
                return mapper.convertValue(details, CodeSubmission.class);
            case text:
                return mapper.convertValue(details, TextSubmission.class);
            case multipleChoice:
                return mapper.convertValue(details, MultipleChoiceSubmission.class);
            default:
                throw new IllegalArgumentException("Cannot determine question type");
        }
    }
}
