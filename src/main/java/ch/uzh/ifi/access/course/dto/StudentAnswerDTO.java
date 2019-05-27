package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.model.workspace.CodeAnswer;
import ch.uzh.ifi.access.course.model.workspace.MultipleChoiceAnswer;
import ch.uzh.ifi.access.course.model.workspace.StudentAnswer;
import ch.uzh.ifi.access.course.model.workspace.TextAnswer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswerDTO {

    private ExerciseType type;

    private JsonNode details;

    public StudentAnswer createStudentAnswer() {
        ObjectMapper mapper = new ObjectMapper();
        switch (type) {
            case code:
                return mapper.convertValue(details, CodeAnswer.class);
            case text:
                return mapper.convertValue(details, TextAnswer.class);
            case multipleChoice:
                return mapper.convertValue(details, MultipleChoiceAnswer.class);
            default:
                throw new IllegalArgumentException("Cannot determine question type");
        }
    }
}
