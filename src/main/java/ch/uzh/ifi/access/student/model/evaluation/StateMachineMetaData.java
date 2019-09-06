package ch.uzh.ifi.access.student.model.evaluation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "statemachineMetadata")
public class StateMachineMetaData {

    private String machineId;
    private String submissionId;
    private String userId;

}
