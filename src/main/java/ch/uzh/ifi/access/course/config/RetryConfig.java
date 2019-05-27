package ch.uzh.ifi.access.course.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.listener.RetryListenerSupport;

@EnableRetry
@Configuration
public class RetryConfig {
    private static final Logger logger = LoggerFactory.getLogger(RetryConfig.class);

    @Bean
    public RetryListenerSupport retryListenerSupport() {
        return new RetryListenerSupport() {
            @Override
            public <T, E extends Throwable> void onError(
                    RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                logger.warn("Count {}: retryable method {} threw exception {}",
                        context.getRetryCount(),
                        context.getAttribute("context.name"),
                        throwable.toString());
                super.onError(context, callback, throwable);
            }
        };
    }
}
