package ch.uzh.ifi.access.keycloak;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.representations.idm.GroupRepresentation;

import java.util.List;

public class Group {

    private static final String STUDENTS_GROUP_NAME = "students";
    private static final String AUTHORS_GROUP_NAME = "authors";

    private GroupRepresentation course;

    private GroupRepresentation students;

    private GroupRepresentation authors;

    private GroupsResource resource;

    public Group(GroupRepresentation course, GroupRepresentation students, GroupRepresentation authors, GroupsResource resource) {
        this.course = course;
        this.students = students;
        this.authors = authors;

        this.resource = resource;
    }

    public String getName() {
        return course.getName();
    }

    public String getStudentsGroupId() {
        return students.getId();
    }

    public String getAuthorsGroupId() {
        return authors.getId();
    }

    public Users getStudents() {
        return new Users(resource.group(students.getId()).members(), List.of());
    }

    public Users getAuthors() {
        return new Users(resource.group(authors.getId()).members(), List.of());
    }

    static Group create(final String title, GroupsResource resource) {
        GroupRepresentation courseRepresentation = new GroupRepresentation();
        courseRepresentation.setName(title);
        String courseGroupId = Utils.getCreatedId(resource.add(courseRepresentation));
        GroupResource course = resource.group(courseGroupId);

        GroupRepresentation students = new GroupRepresentation();
        students.setName(STUDENTS_GROUP_NAME);

        GroupRepresentation authors = new GroupRepresentation();
        authors.setName(AUTHORS_GROUP_NAME);

        String studentsGroupId = Utils.getCreatedId(course.subGroup(students));
        String authorsGroupId = Utils.getCreatedId(course.subGroup(authors));
        GroupRepresentation studentsGroup = resource.group(studentsGroupId).toRepresentation();
        GroupRepresentation authorsGroup = resource.group(authorsGroupId).toRepresentation();

        return new Group(course.toRepresentation(),
                studentsGroup,
                authorsGroup,
                resource);
    }

}
