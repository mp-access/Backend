package ch.uzh.ifi.access.student.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExecResult {

    private String stdout;
    private String stderr;

    public ExecResult(String stdout) {
        this.stdout = stdout;
    }
}
