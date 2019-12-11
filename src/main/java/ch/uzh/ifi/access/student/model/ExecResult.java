package ch.uzh.ifi.access.student.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecResult {

    private String stdout;
    private String testLog;

    private String usedConsoleCommand;
    private String usedTestCommand;

    @JsonIgnore
    private String evalLog;

}
