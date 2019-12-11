package ch.uzh.ifi.access.student.dto;

import lombok.Data;

@Data
public class UserMigrationResult {

    private String from;

    private String to;

    private long numberOfSubmissionsToMigrate;

    private long numberOfSubmissionsMigrated;

    private boolean success;

    public UserMigrationResult(String from, String to) {
        this.from = from;
        this.to = to;
    }
}
