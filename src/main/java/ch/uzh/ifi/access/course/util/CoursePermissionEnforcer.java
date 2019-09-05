package ch.uzh.ifi.access.course.util;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.AssignmentMetadataDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoursePermissionEnforcer {

    public Optional<AssignmentMetadataDTO> shouldAccessAssignment(AssignmentMetadataDTO assignment, String courseId, CourseAuthentication authentication) {
        if (assignment.isPublished() || authentication.hasAdminAccess(courseId)) {
            return Optional.of(assignment);
        }
        return Optional.empty();
    }
}
