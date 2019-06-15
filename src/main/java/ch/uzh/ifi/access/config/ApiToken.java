package ch.uzh.ifi.access.config;

import lombok.Data;

import java.time.Instant;

@Data
public class ApiToken {

    private String id;

    private String key;

    private String owner;

    private String username;

    private Instant created;

    private boolean revoked;
}
