package ch.uzh.ifi.access.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class HeaderApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-Hub-Signature";

    private static final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/courses/**/update", HttpMethod.POST.toString());

    private final ApiTokenAuthenticationProvider authenticationProvider;

    public HeaderApiKeyFilter(ApiTokenAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isSupportedPathAndMethod(request)) {

            String githubWebHookHeader = request.getHeader(HEADER_NAME);
            if (authentication == null && isSupportedHeader(githubWebHookHeader)) {
                Authentication auth =
                        authenticationProvider.authenticate(
                                new ApiTokenAuthenticationProvider.GithubHeaderAuthentication(githubWebHookHeader, null));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSupportedPathAndMethod(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    private boolean isSupportedHeader(String header) {
        return header != null && header.startsWith("sha1=");
    }
}
