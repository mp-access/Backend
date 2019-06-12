package ch.uzh.ifi.access.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final SecurityProperties securityProperties;

    public SwaggerConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ch.uzh.ifi"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())

                .securitySchemes(List.of(securityScheme()))
                .securityContexts(List.of(securityContext()));

    }

    private SecurityScheme securityScheme() {
        GrantType grantType = new AuthorizationCodeGrantBuilder()
                .tokenEndpoint(new TokenEndpoint(securityProperties.getTokenEndpoint(), "oauthtoken"))
                .tokenRequestEndpoint(
                        new TokenRequestEndpoint(securityProperties.getAuthorizationEndpoint(), "access-frontend", ""))
                .build();

        return new OAuthBuilder().name("spring_oauth")
                .grantTypes(List.of(grantType))
                .scopes(Arrays.asList(scopes()))
                .build();
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[]{
                new AuthorizationScope("openid", "OpenId")};
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(
                        List.of(new SecurityReference("spring_oauth", scopes())))
                .forPaths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return new ApiInfo(
                "Course & Student API",
                "",
                formatter.format(LocalDateTime.now()),
                "",
                null,
                "", "", Collections.emptyList());
    }
}