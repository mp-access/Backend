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

public class HeaderApiKeyFilterTest {

    private HeaderApiKeyFilter filter = new HeaderApiKeyFilter(new ApiTokenAuthenticationProvider());

    @Test
    public void doFilterInternal() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.toString());
        request.setPathInfo("/courses/1/update");

        final String hash = "sha1=asdf";
        request.addHeader("X-Hub-Signature", hash);
        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertThat(authentication.getPrincipal()).isEqualTo(hash);
        Assertions.assertThat(authentication).isInstanceOf(ApiTokenAuthenticationProvider.GithubHeaderAuthentication.class);
    }

    @Test
    public void doFilterInternalNoHeaderSet() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.toString());
        request.setPathInfo("/courses/1/update");

        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void doFilterInternalWrongHashingAlgorithm() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.toString());
        request.setPathInfo("/courses/1/update");
        request.addHeader("X-Hub-Signature", "md5=asdf");

        filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}