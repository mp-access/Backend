package ch.uzh.ifi.access.student;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Data
@Configuration
@ConfigurationProperties(prefix = "submission.eval")
public class SubmissionProperties {

    private int threadPoolSize;

    private int maxPoolSize;

    private int queueCapacity;

    private boolean userRateLimit;
}