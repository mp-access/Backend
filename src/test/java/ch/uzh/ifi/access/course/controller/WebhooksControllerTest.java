//package ch.uzh.ifi.access.course.controller;
//
//import ch.uzh.ifi.access.course.model.Course;
//import ch.uzh.ifi.access.course.service.CourseService;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static org.mockito.Mockito.doReturn;
//
//@SpringBootTest(classes = {WebhooksController.class})
//class WebhooksControllerTest {
//
//    protected static final List<String> GITHUB_URLS = List.of("html_url", "clone_url", "ssh_url");
//    protected static final List<String> GITLAB_URLS = List.of("homepage", "git_http_url", "git_ssh_url");
//
//    ObjectMapper mapper = new ObjectMapper();
//    @MockBean
//    CourseService courseService;
//
//    @Autowired
//    WebhooksController webhooksController;
//
//    @Test
//    void parseGitlabPayload() {
//        String homepage = "https://gitlab.com/alexhofmann/testprivaterepo";
//        String git_http_url = "https://gitlab.com/alexhofmann/testprivaterepo.git";
//        String git_ssh_url = "git@gitlab.com:alexhofmann/testprivaterepo.git";
//        Course course = new Course("Course 1");
//        course.setGitURL(git_http_url);
//
//        JsonNode payload = mapper.valueToTree(Map.of("repository",
//                Map.of("homepage", homepage, "git_http_url", git_http_url, "git_ssh_url", git_ssh_url)));
//
//        doReturn(List.of(course)).when(courseService).getAllCourses();
//        ResponseEntity<String> returnedEntity = webhooksController.updateCourse(payload, gitlabHeaderAuthentication);
//
//        Set<String> returnedURLs = GITLAB_URLS.stream()
//                .map(url -> payload.get("repository").get(url).asText()).collect(Collectors.toSet());
//        Assertions.assertEquals(Set.of(homepage, git_http_url, git_ssh_url), returnedURLs);
//        Assertions.assertEquals(course.getId(), returnedEntity.getBody());
//    }
//
//    @Test
//    void parseGithubPayload() {
//        String html_url = "https://github.com/mp-access/Mock-Course";
//        String clone_url = "https://github.com/mp-access/Mock-Course.git";
//        String ssh_url = "git@github.com:mp-access/Mock-Course.git";
//        Course course = new Course("Course 1");
//        course.setGitURL(clone_url);
//
//        JsonNode payload = mapper.valueToTree(Map.of("repository",
//                Map.of("html_url", html_url, "clone_url", clone_url, "ssh_url", ssh_url)));
//
//        doReturn(true).when(githubHeaderAuthentication).matchesHmacSignature(payload.toString());
//        doReturn(List.of(course)).when(courseService).getAllCourses();
//        ResponseEntity<String> returnedEntity = webhooksController.updateCourse(payload);
//
//        Set<String> returnedURLs = GITHUB_URLS.stream()
//                .map(url -> payload.get("repository").get(url).asText()).collect(Collectors.toSet());
//        Assertions.assertEquals(Set.of(html_url, clone_url, ssh_url), returnedURLs);
//        Assertions.assertEquals(course.getId(), returnedEntity.getBody());
//    }
//}