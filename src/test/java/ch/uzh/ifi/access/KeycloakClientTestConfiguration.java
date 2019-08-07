package ch.uzh.ifi.access;

import ch.uzh.ifi.access.config.SecurityProperties;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;

import javax.ws.rs.ClientErrorException;

public class KeycloakClientTestConfiguration {

    private static final String REALM_NAME = "testing";

    public KeycloakClient testClient() {
        return new KeycloakClient(securityProperties(), REALM_NAME, courseProperties());
    }

    private SecurityProperties securityProperties() {
        SecurityProperties properties = new SecurityProperties();
        properties.setAuthServer("http://localhost:9999/auth");
        return properties;
    }

    public CourseServiceSetup.CourseProperties courseProperties() {
        CourseServiceSetup.CourseProperties courseProperties = new CourseServiceSetup.CourseProperties();
        courseProperties.setInitOnStartup(false);
        courseProperties.setDefaultPassword("test");
        courseProperties.setUseDefaultPasswordForNewAccounts(true);
        return courseProperties;
    }

    public void createTestRealm() {
        RealmRepresentation testRealm = new RealmRepresentation();
        testRealm.setEnabled(true);
        testRealm.setRealm(REALM_NAME);

        Keycloak keycloak = keycloak();
        try {
            keycloak.realms().create(testRealm);
        } catch (ClientErrorException e) {
            RealmResource realm = keycloak.realms().realm(REALM_NAME);
            realm.remove();
            keycloak.realms().create(testRealm);
        }
    }

    public void removeTestRealm() {
        RealmResource realm = keycloak().realms().realm(REALM_NAME);
        realm.remove();
    }

    private Keycloak keycloak() {
        return KeycloakClient.keycloak(securityProperties());
    }

    public RealmResource getRealm() {
        return keycloak().realm(REALM_NAME);
    }

}
