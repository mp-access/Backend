package ch.uzh.ifi.access.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(prefix = "rest.security", value = "enabled", havingValue = "true")
@Import({SecurityProperties.class})
public class SecurityConfigurer extends ResourceServerConfigurerAdapter {

    private final ResourceServerProperties resourceServerProperties;

    private final SecurityProperties securityProperties;

    private final HeaderApiKeyFilter filter;

    public SecurityConfigurer(ResourceServerProperties resourceServerProperties, SecurityProperties securityProperties, HeaderApiKeyFilter filter) {
        this.resourceServerProperties = resourceServerProperties;
        this.securityProperties = securityProperties;
        this.filter = filter;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceServerProperties.getResourceId());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        final String[] permittedPaths = new String[]{"/info", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**"};

        http.cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .csrf()
                .disable()
                .addFilterAfter(filter, AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeRequests()
                .antMatchers(permittedPaths)
                .permitAll()
                .antMatchers(securityProperties.getApiMatcher())
                .authenticated();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        if (null != securityProperties.getCorsConfiguration()) {
            source.registerCorsConfiguration("/**", securityProperties.getCorsConfiguration());
        }
        return source;
    }

    @Bean
    public JwtAccessTokenCustomizer jwtAccessTokenCustomizer(ObjectMapper mapper) {
        return new JwtAccessTokenCustomizer(mapper);
    }

    @Bean
    public JwkTokenStore jwkTokenStore(@Value("${security.oauth2.resource.jwk.key-set-uri}") String jwkSetUrl, JwtAccessTokenCustomizer accessTokenCustomizer) {
        return new JwkTokenStore(jwkSetUrl, accessTokenCustomizer);
    }
}