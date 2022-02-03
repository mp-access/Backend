//package ch.uzh.ifi.access.keycloak;
//
//import lombok.extern.slf4j.Slf4j;
//import org.keycloak.admin.client.resource.GroupResource;
//import org.keycloak.admin.client.resource.GroupsResource;
//import org.keycloak.admin.client.resource.RealmResource;
//import org.keycloak.representations.idm.GroupRepresentation;
//import org.keycloak.representations.idm.RoleRepresentation;
//
//import javax.ws.rs.NotFoundException;
//import java.util.List;
//
//@Slf4j
//public class Group {
//
//    private static final String STUDENTS_GROUP_NAME = "students";
//    private static final String ASSISTANTS_GROUP_NAME = "assistants";
//    private static final String ADMINS_GROUP_NAME = "admins";
//
//    private GroupRepresentation course;
//
//    private GroupRepresentation students;
//
//    private GroupRepresentation assistants;
//
//    private GroupRepresentation admins;
//
//    private GroupsResource resource;
//
//    public Group(GroupRepresentation course, GroupRepresentation students, GroupRepresentation assistants, GroupRepresentation admins, GroupsResource resource) {
//        this.course = course;
//        this.students = students;
//        this.assistants = assistants;
//        this.admins = admins;
//        this.resource = resource;
//    }
//
//    public String getName() {
//        return course.getName();
//    }
//
//    public String getStudentsGroupId() {
//        return students.getId();
//    }
//
//    public String getAssistantsGroupId() {
//        return assistants.getId();
//    }
//
//    public String getAdminsGroupId() {
//        return admins.getId();
//    }
//
//    public Users getStudents() {
//        return new Users(resource.group(students.getId()).members(), List.of());
//    }
//
//    public Users getAssistants() {
//        return new Users(resource.group(assistants.getId()).members(), List.of());
//    }
//
//    public Users getAdmins() {
//        return new Users(resource.group(admins.getId()).members(), List.of());
//    }
//
//    static Group create(final String courseId, String title, RealmResource realmResource) {
//        GroupsResource resource = realmResource.groups();
//        GroupRepresentation courseRepresentation = new GroupRepresentation();
//        courseRepresentation.setName(courseId);
//        courseRepresentation.singleAttribute("Title", title);
//        String courseGroupId = Utils.getCreatedId(resource.add(courseRepresentation));
//        GroupResource course = resource.group(courseGroupId);
//
//        // Make sure that 'students' and authors do not appear in the actual group name
//        GroupRepresentation students = new GroupRepresentation();
//        students.setName(String.format("%s - %s", title.replace(STUDENTS_GROUP_NAME, ""), STUDENTS_GROUP_NAME));
//        students.singleAttribute("Title", title);
//
//        GroupRepresentation assistants = new GroupRepresentation();
//        assistants.setName(String.format("%s - %s", title.replace(ASSISTANTS_GROUP_NAME, ""), ASSISTANTS_GROUP_NAME));
//        assistants.singleAttribute("Title", title);
//
//        GroupRepresentation admins = new GroupRepresentation();
//        admins.setName(String.format("%s - %s", title.replace(ADMINS_GROUP_NAME, ""), ADMINS_GROUP_NAME));
//        admins.singleAttribute("Title", title);
//
//        String studentsGroupId = Utils.getCreatedId(course.subGroup(students));
//        String assistantsGroupId = Utils.getCreatedId(course.subGroup(assistants));
//        String adminsGroupId = Utils.getCreatedId(course.subGroup(admins));
//        GroupRepresentation studentsGroup = resource.group(studentsGroupId).toRepresentation();
//        GroupRepresentation assistantsGroup = resource.group(assistantsGroupId).toRepresentation();
//        GroupRepresentation adminsGroup = resource.group(adminsGroupId).toRepresentation();
//
//        try {
//            RoleRepresentation analyticsRole = createRoleIfNotExists("analytics", realmResource);
//            resource.group(adminsGroupId).roles().realmLevel().add(List.of(analyticsRole));
//        } catch (Exception e) {
//            log.warn("Failed to create analytics role and to assign it to the admin group {}", admins.getName(), e);
//        }
//
//        return new Group(course.toRepresentation(),
//                studentsGroup,
//                assistantsGroup,
//                adminsGroup,
//                resource);
//    }
//
//    private static RoleRepresentation createRoleIfNotExists(String roleName, RealmResource realmResource) {
//        RoleRepresentation analyticsRole;
//        try {
//            analyticsRole = realmResource.roles().get(roleName).toRepresentation();
//        } catch (NotFoundException e) {
//            analyticsRole = createRole(roleName, realmResource);
//        }
//        return analyticsRole;
//    }
//
//
//    private static RoleRepresentation createRole(String name, RealmResource realmResource) {
//        RoleRepresentation analyticsRole = new RoleRepresentation();
//        analyticsRole.setName(name);
//        realmResource.roles().create(analyticsRole);
//        return realmResource.roles().get(name).toRepresentation();
//    }
//
//}
