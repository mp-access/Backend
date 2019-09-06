package ch.uzh.ifi.access.student.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecResult {

    private String stdout;
    private String testLog;

    @JsonIgnore
    private String evalLog;

}
