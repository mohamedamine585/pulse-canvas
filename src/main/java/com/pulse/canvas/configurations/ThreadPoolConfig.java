package com.pulse.canvas.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    int availableCores = Runtime.getRuntime().availableProcessors();  // Get number of available cores



    public ThreadPoolTaskExecutor canvasThreadPool() {
        System.out.println(availableCores);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(7);  // Set the core pool size for canvas handling
        executor.setMaxPoolSize(7);   // Set the max pool size for canvas handling
        executor.setQueueCapacity(10); // Set queue capacity for canvas tasks
        executor.setThreadNamePrefix("CanvasThread-");
        return executor;
    }

    public ThreadPoolTaskExecutor dbThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);  // Set the core pool size for DB operations
        executor.setMaxPoolSize(1);   // Set the max pool size for DB operations
        executor.setQueueCapacity(5); // Set queue capacity for DB tasks
        executor.setThreadNamePrefix("DBThread-");
        return executor;
    }
}
