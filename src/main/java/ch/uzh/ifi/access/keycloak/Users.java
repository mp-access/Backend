package ch.uzh.ifi.access.keycloak;

import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Users {

    private List<UserRepresentation> users;

    private int usersCreated;

    public Users(List<UserRepresentation> existingUsers, List<UserRepresentation> createdUsers) {
        this.users = new ArrayList<>(existingUsers);
        users.addAll(createdUsers);

        this.usersCreated = createdUsers.size();
    }

    public Users(List<UserRepresentation> users) {
        this.users = users;
    }

    public Stream<UserRepresentation> stream() {
        return users.stream();
    }

    public int getUsersCreated() {
        return usersCreated;
    }

    public int size() {
        return users.size();
    }

    public List<String> emailAddresses() {
        return users.stream().map(UserRepresentation::getEmail).collect(Collectors.toList());
    }

    public void enrollUsersInGroup(String groupId, UsersResource resource) {
        for (UserRepresentation user : this.users) {
            resource.get(user.getId()).joinGroup(groupId);
        }
    }
}
