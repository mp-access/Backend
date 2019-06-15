package ch.uzh.ifi.access.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class HeaderApiKeyAuthenticationConfiguration {

//    protected static class ApiKeyAuthenticationManager extends OAuth2AuthenticationManager {
//
//        private ApiTokenRepository repository;
//
//        @Override
//        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//            try {
//                return super.authenticate(authentication);
//            } catch (Exception e) {
//                // OAuth2 authentication failed
//
//
//                return new HeaderApiKeyFilter.GithubHeaderAuthentication("testUser", "");
//            }
//        }
//    }
//
//    protected static class GithubHeaderAuthentication extends PreAuthenticatedAuthenticationToken {
//        public GithubHeaderAuthentication(Object aPrincipal, Object aCredentials) {
//            super(aPrincipal, aCredentials);
//        }
//    }
}
