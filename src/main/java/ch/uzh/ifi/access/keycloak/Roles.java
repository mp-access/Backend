package ch.uzh.ifi.access.keycloak;

import ch.uzh.ifi.access.student.controller.AssistantController;
import lombok.Getter;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RoleRepresentation.Composites;
import java.util.List;
import java.util.Set;

public class Roles {

    /**
     * Basic user roles, corresponding to the role names as defined in the Keycloak realm. These 3 roles are created
     * automatically when the app is deployed via the scripts available at mp-access/Infrastructure.
     * @see <a href=https://github.com/mp-access/Infrastructure />
     */
    public static final String STUDENT_ROLE = "student";
    public static final String ASSISTANT_ROLE = "assistant";
    public static final String ADMIN_ROLE = "course-admin";

    @Getter
    String courseRoleName;
    RolesResource rolesResource;

    public Roles(String courseRoleName, RolesResource rolesResource) {
        this.courseRoleName = courseRoleName;
        this.rolesResource = rolesResource;
    }

    public String getUserRoleNameForCourse(String userRole) {
        return String.join("-", courseRoleName, userRole);
    }

    private RoleRepresentation getUserRoleForCourse(String userRole) {
        return rolesResource.get(getUserRoleNameForCourse(userRole)).toRepresentation();
    }

    public List<RoleRepresentation> getStudentRolesForCourse() {
        return List.of(getUserRoleForCourse(STUDENT_ROLE));
    }

    public List<RoleRepresentation> getAssistantRolesForCourse() {
        return List.of(getUserRoleForCourse(ASSISTANT_ROLE));
    }

    /**
     * Admins are assigned both the course assistant role and the course admin role in order to simplify the
     * authorization check - it is sufficient to check if the user has an assistant role in order to grant access
     * to both assistants and admins.
     * @see AssistantController   for endpoints that have admins-only authorization checks
     */
    public List<RoleRepresentation> getAdminRolesForCourse() {
        return List.of(getUserRoleForCourse(ASSISTANT_ROLE), getUserRoleForCourse(ADMIN_ROLE));
    }

    /**
     * Create a representation of a new course user role for the input {@param userRoleName}, which is one of the 3
     * basic user roles as defined above (STUDENT_ROLE, ASSISTANT_ROLE or ADMIN_ROLE).
     *
     * Every new course user role is a composite of 2 roles:
     * (1) The basic course role, for example "mock-course-hs-2022"
     * (2) The corresponding basic user role, for example "student"
     *
     * The name of the new course user role is also composed of these 2 roles, for example "mock-course-hs-2022-student".
     * Since the created role is a composite, a user that is assigned a course user role will also be automatically
     * assigned the 2 basic composite roles.
     *
     * @param userRoleName  basic user role name (STUDENT_ROLE, ASSISTANT_ROLE or ADMIN_ROLE)
     * @return              representation of the new course user role
     */
    private RoleRepresentation createCourseUserRole(String userRoleName) {
        RoleRepresentation userRole = new RoleRepresentation();
        userRole.setName(getUserRoleNameForCourse(userRoleName));
        userRole.setComposite(true);
        Composites userRoleComposites = new Composites();
        userRoleComposites.setRealm(Set.of(courseRoleName, userRoleName));
        userRole.setComposites(userRoleComposites);
        return userRole;
    }

    /**
     * Creates 4 new roles per course: basic course role, course student role, course assistant role and course
     * admin role. The basic course role is created directly and is not a composite, while the 3 course user roles
     * are composite roles created from the returned RoleRepresentation.
     * @see #createCourseUserRole(String)
     */
    public void createCourseRoles() {
        RoleRepresentation courseRole = new RoleRepresentation();
        courseRole.setName(courseRoleName);
        rolesResource.create(courseRole);
        rolesResource.create(createCourseUserRole(STUDENT_ROLE));
        rolesResource.create(createCourseUserRole(ASSISTANT_ROLE));
        rolesResource.create(createCourseUserRole(ADMIN_ROLE));
    }
}
