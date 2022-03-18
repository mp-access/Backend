package ch.uzh.ifi.access.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "access")
public class AccessProperties {

    private List<String> repositories;

    private String cacheDir;

    private String hmac;

    private String githubWebhook;

    private String gitlabWebhook;

    private boolean initOnStartup;

    private boolean useDefaultPasswordForNewAccounts;

    private String defaultPassword;

    private String adminCLIUsername;

    private String adminCLIPassword;

}
