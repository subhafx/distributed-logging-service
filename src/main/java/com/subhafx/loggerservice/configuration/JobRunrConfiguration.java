package com.subhafx.loggerservice.configuration;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRunrConfiguration {
    @Bean
    public JobScheduler initJobRunr(ApplicationContext applicationContext) {
        return JobRunr.configure()
                .useStorageProvider(new InMemoryStorageProvider())
                .useJobActivator(applicationContext::getBean)
                /*
                * Use 1 worker only, to overcome the race condition
                */
                .useBackgroundJobServer(1)
                .useDashboard()
                .initialize();
    }
}
