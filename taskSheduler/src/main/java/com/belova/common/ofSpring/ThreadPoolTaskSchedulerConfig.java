package com.belova.common.ofSpring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
public class ThreadPoolTaskSchedulerConfig {
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Bean(name = "cronTriggerToBackupData")
    public CronTrigger cronTriggerToBackupData() {
        //return new CronTrigger("0 0/2 * * * MON-FRI");
        return new CronTrigger("*/15 * * * * MON-FRI");
    }

    @Bean(name = "cronTriggerToNotification")
    public CronTrigger cronTriggerToNotification() {
        return new CronTrigger("0 0/1 * * * *");
    }
}
