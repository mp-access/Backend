package ch.uzh.ifi.access.student.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecResult {

    private String stdout;
    private String stderr;

}
