package ch.uzh.ifi.access.course.util;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public class CoursePermissionEnforcerTest {

    private CoursePermissionEnforcer enforcer = new CoursePermissionEnforcer();

    private Course course = new Course("");

    @Test
    public void shouldNotAccessNotPublishedAssignmentNormalStudent() {
        Assignment assignment = notYetPublishedAssignment();

        GrantedCourseAccess access = studentAccess();
        CourseAuthentication authentication = TestObjectFactory.courseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(new AssignmentMetadataDTO(assignment), course.getId(), authentication);

        Assertions.assertThat(hasAccess).isEmpty();
    }

    @Test
    public void shouldAccessPublishedAssignmentNormalStudent() {
        Assignment assignment = publishedAssignment();

        AssignmentMetadataDTO dto = new AssignmentMetadataDTO(assignment);
        GrantedCourseAccess access = studentAccess();
        CourseAuthentication authentication = TestObjectFactory.courseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(dto, course.getId(), authentication);

        Assertions.assertThat(hasAccess).isNotEmpty();
        Assertions.assertThat(hasAccess).hasValue(dto);
    }

    @Test
    public void shouldAccessNotPublishedAssignmentAdmin() {
        Assignment assignment = notYetPublishedAssignment();

        AssignmentMetadataDTO dto = new AssignmentMetadataDTO(assignment);
        GrantedCourseAccess access = adminAccess();
        CourseAuthentication authentication = TestObjectFactory.courseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(dto, course.getId(), authentication);

        Assertions.assertThat(hasAccess).hasValue(dto);
    }

    @Test
    public void shouldAccessPublishedAssignmentAdmin() {
        Assignment assignment = publishedAssignment();

        AssignmentMetadataDTO dto = new AssignmentMetadataDTO(assignment);
        GrantedCourseAccess access = adminAccess();
        CourseAuthentication authentication = TestObjectFactory.courseAuthentication(Set.of(access));
        Optional<AssignmentMetadataDTO> hasAccess = enforcer.shouldAccessAssignment(dto, course.getId(), authentication);

        Assertions.assertThat(hasAccess).isNotEmpty();
        Assertions.assertThat(hasAccess).hasValue(dto);
    }

    private Assignment publishedAssignment() {
        return Assignment.builder().publishDate(LocalDateTime.now().minusYears(1)).build();
    }

    private Assignment notYetPublishedAssignment() {
        return Assignment.builder().publishDate(LocalDateTime.now().plusYears(1)).build();
    }

    private GrantedCourseAccess adminAccess() {
        return new GrantedCourseAccess(course.getId(), false, true);
    }

    private GrantedCourseAccess studentAccess() {
        return new GrantedCourseAccess(course.getId(), true, false);
    }
}