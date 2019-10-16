package ch.uzh.ifi.access.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * How ThreadPoolTaskExecutor works:
 * 1. Tasks are submitted to the executor which places them in its queue of size {@link AsyncConfig#QUEUE_CAPACITY}
 * 2. If there are less than {@link AsyncConfig#THREAD_POOL_SIZE} threads occupied, the executor pops (FIFO)
 * the task at the top and assigns the task to a thread
 * 3. If there are no free threads and the queue is full, the thread pool increases in size up to {@link AsyncConfig#MAX_POOL_SIZE}
 * 4. If there are no available threads in the pool,
 * the queue is empty and the pool has reached {@link AsyncConfig#MAX_POOL_SIZE}, then the task is dropped with an exception
 */
@Configuration
@EnableAsync
@Data
public class AsyncConfig {

    @Value("${submission.eval.thread-pool-size}")
    private int THREAD_POOL_SIZE = 10;

    @Value("${submission.eval.max-pool-size}")
    private int MAX_POOL_SIZE = 20;

    /**
     * Size of queue for threads in case more than {@link AsyncConfig#THREAD_POOL_SIZE} threads are currently occupied
     */
    @Value("${submission.eval.queue-capacity}")
    private int QUEUE_CAPACITY = 500;

    @Bean("evalWorkerExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("eval-worker-");
        executor.setCorePoolSize(THREAD_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    @Bean("courseUpdateWorkerExecutor")
    public Executor getCourseUpdateExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("course-update-worker-");
        executor.setCorePoolSize(THREAD_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.initialize();
        return executor;
    }
}
