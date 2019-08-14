package ch.uzh.ifi.access.student.dto;

import ch.uzh.ifi.access.student.model.StudentSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResults {

    private String userId;
    private String assignmentId;
    private int maxScore;
    private List<StudentSubmission> gradedSubmissions;

    public double getStudentScore() {
        return gradedSubmissions.stream().mapToDouble(sub -> sub.getResult().getScore()).sum();
    }

}
