//package ch.uzh.ifi.access.config;
//
//import org.springframework.http.HttpMethod;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class HeaderApiKeyFilter extends OncePerRequestFilter {
//
//    private static final String GITHUB_HEADER_NAME = "X-Hub-Signature";
//
//    private static final String GITLAB_HEADER_NAME = "X-Gitlab-Token";
//
//    private static final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/webhooks/courses/**/update/{github|gitlab}", HttpMethod.POST.toString());
//
//    private final ApiTokenAuthenticationProvider authenticationProvider;
//
//    public HeaderApiKeyFilter(ApiTokenAuthenticationProvider authenticationProvider) {
//        this.authenticationProvider = authenticationProvider;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (isSupportedPathAndMethod(request)) {
//
//            final String githubWebHookHeader = request.getHeader(GITHUB_HEADER_NAME);
//            final String gitlabWebHookHeader = request.getHeader(GITLAB_HEADER_NAME);
//            boolean isGithub = isSupportedGithubHeader(githubWebHookHeader);
//            boolean isGitlab = isSupportedGitlabHeader(gitlabWebHookHeader);
//
//            if (authentication == null && (isGithub || isGitlab)) {
//                final String principal = isGithub ? githubWebHookHeader : gitlabWebHookHeader;
//                authentication = authenticationProvider.authenticate(
//                        new ApiTokenAuthenticationProvider.HeaderAuthentication(principal, isGithub, isGitlab));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } else {
//                SecurityContextHolder.clearContext();
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private boolean isSupportedPathAndMethod(HttpServletRequest request) {
//        return requestMatcher.matches(request);
//    }
//
//    /**
//     * A github header starts with 'sha1=' to indicate the hashing algorithm
//     */
//    private boolean isSupportedGithubHeader(String header) {
//        return header != null && header.startsWith("sha1=");
//    }
//
//    /**
//     * For gitlab it is enough if the header is not null
//     */
//    private boolean isSupportedGitlabHeader(String header) {
//        return header != null;
//    }
//}
