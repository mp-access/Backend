package ch.uzh.ifi.access.course;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.dto.CourseMetadataDTO;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
public class CoursePermissionsAdvice {

    private static final Logger logger = LoggerFactory.getLogger(CoursePermissionsAdvice.class);

    @AfterReturning(returning = "courses", pointcut = "@annotation(ch.uzh.ifi.access.course.FilterByPublishingDate) && execution(java.util.List<ch.uzh.ifi.access.course.dto.CourseMetadataDTO> ch.uzh..*(..))")
    public void filterByStartDate(Collection<CourseMetadataDTO> courses) {
        logger.debug("filterByStartDate() advice");
        if (courses != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            filterAssignmentsByStartDate(courses, (CourseAuthentication) authentication);
        }
    }

    private void filterAssignmentsByStartDate(Collection<CourseMetadataDTO> courses, CourseAuthentication authentication) {
        courses.forEach(course -> {
            if (course.getAssignments() != null) {
                course
                        .getAssignments()
                        .removeIf(assignment -> {
                            // A course admin of a course has unrestricted access to said course
                            if (authentication.hasAdminAccess(course.getId())) {
                                return false;
                            }
                            return !assignment.isPublished();
                        });
            }
        });
    }
}