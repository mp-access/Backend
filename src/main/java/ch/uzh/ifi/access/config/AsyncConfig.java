package ch.uzh.ifi.access.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Data
public class AsyncConfig {

    @Value("${submission.eval.thread-pool-size}")
    private int THREAD_POOL_SIZE = 10;

    @Bean("evalWorkerExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(THREAD_POOL_SIZE);
        executor.setThreadNamePrefix("evaluation-worker-");
        executor.initialize();
        return executor;
    }
}
