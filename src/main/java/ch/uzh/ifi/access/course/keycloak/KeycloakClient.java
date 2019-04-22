package ch.uzh.ifi.access.course.keycloak;

import ch.uzh.ifi.access.course.config.SecurityProperties;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class KeycloakClient {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakClient.class);

    private SecurityProperties securityProperties;

    private static final String ADMIN_CLIENT_ID = "admin-cli";

    private static final String REALM = "dev";

    private static final String CUSTOM_CLAIM_KEY = "courses";

    public KeycloakClient(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * Iterates over all users of the realm and adds custom claims.
     * This is a test for granting course access:
     * - Parse users list from repository
     * - For each email address
     * - Fetch user by email address
     * - Assign course as custom attribute
     * - Save user
     * <p>
     * A custom client mapper will then add the custom attributes to the access token on login.
     */
    public void addCourseClaims() {
        logger.debug("Adding custom claims to users in realm");
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(securityProperties.getAuthServer())
                .realm("master")
                .username("admin")
                .password("admin")
                .clientId(ADMIN_CLIENT_ID)
                .resteasyClient(
                        new ResteasyClientBuilder().connectionPoolSize(10).build()
                ).build();

        List<UserRepresentation> users = keycloak.realm(REALM).users().list();
        logger.debug("Fetching users");

        users.forEach(user -> {
            Map<String, List<String>> attributes = user.getAttributes();
            if (attributes == null) {
                attributes = new HashMap<>();
            }
            attributes.put(CUSTOM_CLAIM_KEY, List.of("Info1", "Info2"));
            user.setAttributes(attributes);
            updateUser(user, keycloak);
            logger.debug("Assigned claims to user: " + user.getUsername());
        });
    }

    private void updateUser(UserRepresentation user, Keycloak keycloak) {
        UserResource userResource = keycloak.realm(REALM).users().get(user.getId());
        userResource.update(user);
    }
}
