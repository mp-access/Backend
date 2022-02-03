package ch.uzh.ifi.access.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;

import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(prefix = "keycloak", value = "enabled", havingValue = "true")
@ComponentScan(basePackageClasses = KeycloakSpringBootConfigResolver.class)
public class SecurityConfigurer extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * Register Keycloak with the authentication manager and set up a mapping from Keycloak role names to
     * Spring Security's default role naming scheme (with the prefix "ROLE_"). This allows referring to
     * role names exactly as they appear in Keycloak, without having to add the "ROLE_" prefix.
     * @see <a href="https://keycloak.org/docs/latest/securing_apps/index.html#naming-security-roles"/>
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * Use the Spring properties defined in application.properties instead of searching for a "keycloak.json" file.
     * @see <a href="https://keycloak.org/docs/latest/securing_apps/index.html#using-spring-boot-configuration"/>
     */
    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        super.configure(http);
        final String[] permittedPaths = new String[]{"/info", "/v3/api-docs/**", "/swagger-ui/**"};

        http.csrf().disable().authorizeRequests()
                .antMatchers(permittedPaths)
                .permitAll()
                .antMatchers("/**")
                .authenticated();
    }
}