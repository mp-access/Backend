package ch.uzh.ifi.access.config;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Adds to the pre-authenticated authentication object the necessary hmac hasher to verify
 * the signature in the downstream controller. This is done only in the controller since
 * I could not find a way to consume the request body more than once.
 *
 * @author Alexander Hofmann
 */
@Component
public class ApiTokenAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ApiTokenAuthenticationProvider.class);

    /**
     * Shared secret for hmac
     */
    @Value("${GITHUB_WEBHOOK_SECRET:FALLBACK_SECRET_WHICH_SHOULD_BE_TAKEN_FROM_ENV}")
    private String hmacSecret;

    private HmacUtils hmacUtils;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (supports(authentication.getClass())) {
            return new GithubHeaderAuthentication(authentication.getPrincipal(), hmacUtils);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return GithubHeaderAuthentication.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() {
        if (hmacSecret == null || hmacSecret.isEmpty()) {
            throw new UnsupportedOperationException("No hmac secret set! Exiting...");
        }

        if ("FALLBACK_SECRET_WHICH_SHOULD_BE_TAKEN_FROM_ENV".equals(hmacSecret)) {
            logger.warn("Using the fallback secret for development. This should never happen in production.");
        }
        hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, hmacSecret);
    }

    public static class GithubHeaderAuthentication extends PreAuthenticatedAuthenticationToken {

        private HmacUtils hmacMatcher;

        GithubHeaderAuthentication(Object aPrincipal, HmacUtils hmacUtils) {
            super(aPrincipal, "N/A");
            setAuthenticated(true);
            this.hmacMatcher = hmacUtils;
        }

        public boolean matchesHmacSignature(String payload) {
            String hmac = String.format("sha1=%s", hmacMatcher.hmacHex(payload));
            return hmac.equals(getName());
        }
    }
}