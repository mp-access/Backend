//package ch.uzh.ifi.access.config;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//import org.springframework.web.cors.CorsConfiguration;
//
//import java.util.List;
//
//@Component
//@Data
//@Configuration
//@ConfigurationProperties(prefix = "rest.security")
//public class SecurityProperties {
//
//    private boolean enabled;
//
//    private String apiMatcher;
//
//    private Cors cors;
//
//    private String issuerUri;
//
//    private String authServer;
//
//    private String tokenEndpoint;
//
//    private String authorizationEndpoint;
//
//    private String keycloakApiAdmin = "admin";
//
//    private String keycloakApiPassword = "admin";
//
//    private String realm = "access";
//
//    private String frontendClientId= "access-frontend";
//
//    private String redirectUriAfterActions = "localhost:3000";
//
//    public CorsConfiguration getCorsConfiguration() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedMethods(cors.getAllowedMethods());
//        corsConfiguration.setAllowedOrigins(cors.getAllowedOrigins());
//        corsConfiguration.setAllowedHeaders(cors.getAllowedHeaders());
//        corsConfiguration.setMaxAge(cors.getMaxAge());
//
//        return corsConfiguration;
//    }
//
//    @Data
//    @Configuration
//    @ConfigurationProperties(prefix = "rest.security.cors")
//    public static class Cors {
//
//        private List<String> allowedOrigins;
//
//        private List<String> allowedMethods;
//
//        private List<String> allowedHeaders;
//
//        private Long maxAge;
//    }
//
//}