package ch.uzh.ifi.access.course.util;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

public class CoursePermissionEnforcerTest {

    private CoursePermissionEnforcer enforcer = new CoursePermissionEnforcer();

    private Course course = new Course("");

    @Test
    public void shouldNotAccessNotPublishedAssignmentNormalStudent() {
        Assignment assignment = notYetPublishedAssignment();

        GrantedCourseAccess access = studentAccess();
        CourseAuthentication authentication = TestObjectFactory.createCourseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(new AssignmentMetadataDTO(assignment), course.getId(), authentication);

        Assertions.assertTrue(hasAccess.isEmpty());
    }

    @Test
    public void shouldAccessPublishedAssignmentNormalStudent() {
        Assignment assignment = publishedAssignment();

        AssignmentMetadataDTO dto = new AssignmentMetadataDTO(assignment);
        GrantedCourseAccess access = studentAccess();
        CourseAuthentication authentication = TestObjectFactory.createCourseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(dto, course.getId(), authentication);

        Assertions.assertFalse(hasAccess.isEmpty());
        Assertions.assertEquals(dto, hasAccess.get());
    }

    @Test
    public void shouldAccessNotPublishedAssignmentAssistant() {
        Assignment assignment = notYetPublishedAssignment();

        AssignmentMetadataDTO dto = new AssignmentMetadataDTO(assignment);
        GrantedCourseAccess access = assistantAccess();
        CourseAuthentication authentication = TestObjectFactory.createCourseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(dto, course.getId(), authentication);

        Assertions.assertFalse(hasAccess.isEmpty());
        Assertions.assertEquals(dto, hasAccess.get());
    }

    @Test
    public void shouldAccessPublishedAssignmentAssistant() {
        Assignment assignment = publishedAssignment();

        AssignmentMetadataDTO dto = new AssignmentMetadataDTO(assignment);
        GrantedCourseAccess access = assistantAccess();
        CourseAuthentication authentication = TestObjectFactory.createCourseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(dto, course.getId(), authentication);

        Assertions.assertFalse(hasAccess.isEmpty());
        Assertions.assertEquals(dto, hasAccess.get());
    }

    @Test
    public void shouldAccessNotPublishedAssignmentAdmin() {
        Assignment assignment = notYetPublishedAssignment();

        AssignmentMetadataDTO dto = new AssignmentMetadataDTO(assignment);
        GrantedCourseAccess access = adminAccess();
        CourseAuthentication authentication = TestObjectFactory.createCourseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(dto, course.getId(), authentication);

        Assertions.assertFalse(hasAccess.isEmpty());
        Assertions.assertEquals(dto, hasAccess.get());
    }

    @Test
    public void shouldAccessPublishedAssignmentAdmin() {
        Assignment assignment = publishedAssignment();

        AssignmentMetadataDTO dto = new AssignmentMetadataDTO(assignment);
        GrantedCourseAccess access = adminAccess();
        CourseAuthentication authentication = TestObjectFactory.createCourseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(dto, course.getId(), authentication);

        Assertions.assertFalse(hasAccess.isEmpty());
        Assertions.assertEquals(dto, hasAccess.get());
    }

    private Assignment publishedAssignment() {
        Course course = TestObjectFactory.createCourseWithAssignmentAndExercises("");
        return Assignment.builder().publishDate(ZonedDateTime.now().minusYears(1)).build();
    }

    private Assignment notYetPublishedAssignment() {
        Course course = TestObjectFactory.createCourseWithAssignmentAndExercises("");
        return Assignment.builder().publishDate(ZonedDateTime.now().plusYears(1)).build();
    }

    private GrantedCourseAccess adminAccess() {
        return new GrantedCourseAccess(course.getId(), false, false, true);
    }

    private GrantedCourseAccess assistantAccess() {
        return new GrantedCourseAccess(course.getId(), false, true, false);
    }


    private GrantedCourseAccess studentAccess() {
        return new GrantedCourseAccess(course.getId(), true, false, false);
    }
}