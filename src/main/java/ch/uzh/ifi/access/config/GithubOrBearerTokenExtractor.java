package ch.uzh.ifi.access.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;

import javax.servlet.http.HttpServletRequest;

public class GithubOrBearerTokenExtractor implements TokenExtractor {

    private static final Logger logger = LoggerFactory.getLogger(GithubOrBearerTokenExtractor.class);

    private final TokenExtractor bearerTokenExtractor = new BearerTokenExtractor();

    private final TokenExtractor githubTokenExtractor = new GithubTokenExtractor();

    @Override
    public Authentication extract(HttpServletRequest request) {
        TokenExtractor tokenExtractor = extractStrategy(request);
        return tokenExtractor.extract(request);
    }

    private TokenExtractor extractStrategy(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String githubHeader = request.getHeader("X-Hub-Signature");

        // Give precedence to OAuth2 token if both are present
        if (authorizationHeader != null &&
                authorizationHeader.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            return bearerTokenExtractor;
        }

        if (githubHeader != null && githubHeader.toLowerCase().startsWith("sha1=")) {
            return githubTokenExtractor;
        }
        
        return nullTokenExtractor();
    }

    private TokenExtractor nullTokenExtractor() {
        return (request) -> null;
    }
}
