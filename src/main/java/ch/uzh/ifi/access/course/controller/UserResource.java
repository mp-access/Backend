package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserResource {

    @GetMapping
    public ResponseEntity<Principal> getUser(@ApiIgnore Principal principal) {
        return ResponseEntity.ok(principal);
    }

    @GetMapping(path = "/admin")
    public ResponseEntity<String> getAdmin(@ApiIgnore Principal principal) {
        return ResponseEntity.ok("ggwp admin " + principal.getName());
    }

    @GetMapping("/courses")
    public Set<GrantedCourseAccess> getCourses(@ApiIgnore CourseAuthentication authentication) {
        return authentication.getCourseAccesses();
    }
}