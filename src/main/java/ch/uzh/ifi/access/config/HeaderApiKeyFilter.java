package ch.uzh.ifi.access.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeaderApiKeyFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(HeaderApiKeyFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof GithubHeaderAuthentication) {
            GithubHeaderAuthentication custom = (GithubHeaderAuthentication) authentication;
            logger.info("Found custom authentication: " + custom.getPrincipal());
            if ("GOOD".equals(custom.getPrincipal())) {
                authentication.setAuthenticated(true);
            } else {
                SecurityContextHolder.clearContext();
            }
        } else {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    protected static class CustomAuthenticator extends OAuth2AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            try {
                return super.authenticate(authentication);
            } catch (Exception e) {
                return new GithubHeaderAuthentication("testUser", "");
            }
        }
    }

    @SuppressWarnings("serial")
    protected static class GithubHeaderAuthentication extends PreAuthenticatedAuthenticationToken {
        public GithubHeaderAuthentication(Object aPrincipal, Object aCredentials) {
            super("GOOD", aCredentials);
        }
    }
}
