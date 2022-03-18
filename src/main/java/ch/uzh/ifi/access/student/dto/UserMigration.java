package ch.uzh.ifi.access.student.dto;

import lombok.Data;

@Data
public class UserMigration {

    private final String from;

    private final String to;
}
