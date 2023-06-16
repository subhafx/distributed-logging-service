package com.subhafx.loggerservice;

import com.subhafx.loggerservice.configuration.JobRunrConfiguration;
import com.subhafx.loggerservice.configuration.LogIngestBatchConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.UUID;

@Slf4j
@Import(value = {JobRunrConfiguration.class, LogIngestBatchConfiguration.class})
@SpringBootApplication
@EnableScheduling
public class LoggerserviceApplication {

    private final ApplicationContext applicationContext;
    private final JobLauncher jobLauncher;


    @Autowired
    public LoggerserviceApplication(ApplicationContext applicationContext, JobLauncher jobLauncher) {
        this.applicationContext = applicationContext;
        this.jobLauncher = jobLauncher;
    }


    public static void main(String[] args) {
        SpringApplication.run(LoggerserviceApplication.class, args);
    }

    /**
     * @throws JobInstanceAlreadyCompleteException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobParametersInvalidException
     * @throws JobRestartException
     * @apiNote Runs Every 30th seconds and process files of last 30s
     */
    @Scheduled(cron = "*/30 * * * * ?")
    public void importLogsToDB() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job importLogJob = applicationContext.getBean("importLogJob", Job.class);
        String fileNameSuffix = String.valueOf(Math.abs(Calendar.getInstance().get(Calendar.SECOND) - 30));
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("fileNameSuffix", fileNameSuffix)
                .addString("JobId", UUID.randomUUID().toString())
                .addLong("time", System.currentTimeMillis()).toJobParameters();

        JobExecution execution = jobLauncher.run(importLogJob, jobParameters);
        log.debug("STATUS :: " + execution.getStatus());
    }

}
