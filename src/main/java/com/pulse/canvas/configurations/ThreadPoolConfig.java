package com.pulse.canvas.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    private static final int AVAILABLE_CORES = Runtime.getRuntime().availableProcessors();

    public ThreadPoolTaskExecutor canvasThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(7);   // Core thread count for canvas handling
        executor.setMaxPoolSize(7);    // Maximum thread count
        executor.setQueueCapacity(10); // Queue size for pending tasks
        executor.setThreadNamePrefix("CanvasThread-");
        executor.initialize();
        return executor;
    }

    public ThreadPoolTaskExecutor dbThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);   // Single thread for DB operations
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(5);
        executor.setThreadNamePrefix("DBThread-");
        executor.initialize();
        return executor;
    }

   /* @Bean
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }*/
}
