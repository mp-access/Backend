package ch.uzh.ifi.access.config;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class AsyncConfigTest {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfigTest.class);

    @Test
    public void asyncExecutor() {
        AsyncConfig asyncConfig = new AsyncConfig();
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) asyncConfig.getAsyncExecutor();

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            futures.add(executor.submit(testThread(i)));
        }

        while (!futures.stream().allMatch(Future::isDone)) {
            // Wait for threads to finish, otherwise the test will end with the threads still running
        }
    }

    private Runnable testThread(int i) {
        final int sleepInSeconds = 5;
        return () -> {
            logger.info(String.format("Job %d, thread %s going to sleep for %d seconds", i, Thread.currentThread().getName(), sleepInSeconds));
            try {
                Thread.sleep(sleepInSeconds * 1000);
                logger.info(String.format("Job %d, thread %s woke up and is finished", i, Thread.currentThread().getName()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }
}