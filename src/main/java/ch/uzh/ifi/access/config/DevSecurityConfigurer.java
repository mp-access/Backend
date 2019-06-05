package ch.uzh.ifi.access.config;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "rest.security", value = "enabled", havingValue = "false")
public class DevSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new CustomAuthenticationProvider(), BasicAuthenticationFilter.class)

                .authorizeRequests()
                .antMatchers("/**")
                .authenticated()
                .and()
                .csrf().disable();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

    public static class CustomAuthenticationProvider extends OncePerRequestFilter {

        public Authentication authentication() {
            Set<GrantedCourseAccess> grantedCourseAccesses = Set.of();
            OAuth2Request request = new OAuth2Request(Map.of(),
                    "client",
                    List.of(), true,
                    Set.of("openid"),
                    Set.of(), null, null, null);
            Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "---");
            return new CourseAuthentication(request, auth, grantedCourseAccesses, "");
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            SecurityContextHolder.getContext().setAuthentication(authentication());
            filterChain.doFilter(request, response);
        }
    }
}