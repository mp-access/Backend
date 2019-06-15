package ch.uzh.ifi.access.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

public class GithubTokenExtractor implements TokenExtractor {

    @Override
    public Authentication extract(HttpServletRequest request) {
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken("testUser", "");
        return authentication;
    }
}
