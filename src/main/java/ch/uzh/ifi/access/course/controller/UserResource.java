package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import ch.uzh.ifi.access.course.config.CourseAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserResource {

    @GetMapping
    public ResponseEntity<Principal> getUser(Principal principal) {
        return ResponseEntity.ok(principal);
    }

    @GetMapping(path = "/admin")
    public ResponseEntity<String> getAdmin(Principal principal) {
        return ResponseEntity.ok("ggwp admin " + principal.getName());
    }

    @GetMapping("/courses")
    public Set<GrantedCourseAccess> getCourses(CourseAuthentication authentication) {
        return authentication.getCourseAccesses();
    }
}