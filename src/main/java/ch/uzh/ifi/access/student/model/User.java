package ch.uzh.ifi.access.student.model;

import lombok.Data;
import org.keycloak.representations.idm.UserRepresentation;

@Data
public class User implements Comparable<User> {

    private final String id;

    private final String emailAddress;

    public static User of(UserRepresentation userRepresentation) {
        return new User(userRepresentation.getId(), userRepresentation.getUsername());
    }

    @Override
    public int compareTo(User o) {
        return emailAddress.compareTo(o.emailAddress);
    }
}
