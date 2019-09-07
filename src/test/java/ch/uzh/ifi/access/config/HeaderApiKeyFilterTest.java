package ch.uzh.ifi.access.config;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;

public class HeaderApiKeyFilterTest {

    private static final String URL = "/webhooks/courses";

    private HeaderApiKeyFilter filter = new HeaderApiKeyFilter(new ApiTokenAuthenticationProvider());

    @Test
    public void otherPathsShouldBeIgnored() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.toString());
        request.setPathInfo("/foo/bar");
        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertThat(authentication).isNull();
    }

    @Test
    public void githubHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.toString());
        request.setPathInfo(URL + "/1/update/github");

        final String hash = "sha1=asdf";
        request.addHeader("X-Hub-Signature", hash);
        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertThat(authentication.getPrincipal()).isEqualTo(hash);
        Assertions.assertThat(authentication).isInstanceOf(ApiTokenAuthenticationProvider.GithubHeaderAuthentication.class);
    }

    @Test
    public void gitlabHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.toString());
        request.setPathInfo(URL + "/1/update/gitlab");

        final String header = UUID.randomUUID().toString();
        request.addHeader("X-Gitlab-Token", header);
        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertThat(authentication.getPrincipal()).isEqualTo("Gitlab");
        Assertions.assertThat(authentication).isInstanceOf(ApiTokenAuthenticationProvider.GitlabHeaderAuthentication.class);
    }

    @Test
    public void doFilterInternalNoHeaderSet() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.toString());
        request.setPathInfo(URL + "/1/update/github");

        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void githubWrongHashingAlgorithm() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.toString());
        request.setPathInfo(URL + "/1/update/github");
        request.addHeader("X-Hub-Signature", "md5=asdf");

        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}