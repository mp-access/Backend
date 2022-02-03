//package ch.uzh.ifi.access.course.util;
//
//import ch.uzh.ifi.access.course.config.CourseAuthentication;
//import ch.uzh.ifi.access.course.model.HasPublishingDate;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//public class CoursePermissionEnforcer {
//
//    public <T extends HasPublishingDate> Optional<T> shouldAccessAssignment(T assignment, String courseId, CourseAuthentication authentication) {
//        if (assignment.isPublished() || authentication.hasPrivilegedAccess(courseId)) {
//            return Optional.of(assignment);
//        }
//        return Optional.empty();
//    }
//}
