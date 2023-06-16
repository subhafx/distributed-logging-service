package com.subhafx.loggerservice.configuration;


import com.subhafx.loggerservice.batch.FileDeletingTasklet;
import com.subhafx.loggerservice.dto.LogCSVDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Paths;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class LogIngestBatchConfiguration {

    /**
     * @param fileNameSuffix it ranges from 0 to 60
     * @return Resource[] list of possible files
     */
    public Resource[] generateResourceNames(int fileNameSuffix){
        int incrementor = fileNameSuffix < 30 ? 0 : 30 ; // Decides the range 0 - 29 or 30 - 60
        Resource[] inputResources = new Resource[30];
        IntStream
                .range(0,30)
                .forEach( it -> inputResources[it] = new FileSystemResource(
                        Paths.get("./temp/temp_logs_"+(it + incrementor)+".csv").toFile()
                        ));
        return inputResources;
    }

    /**
     * @param fileNameSuffix Passed by job launcher from LoggerserviceApplication::class
     * @return MultiResourceItemReader<LogCSVDTO>
     */
    @StepScope
    @Bean
    public MultiResourceItemReader<LogCSVDTO> multiResourceItemReader(@Value("#{jobParameters['fileNameSuffix']}") String fileNameSuffix)
    {
        MultiResourceItemReader<LogCSVDTO> resourceItemReader = new MultiResourceItemReader<>();
        resourceItemReader.setStrict(false);
        resourceItemReader.setResources(generateResourceNames(Integer.parseInt(fileNameSuffix)));
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }


    @Bean
    public FlatFileItemReader<LogCSVDTO> reader() {
        return new FlatFileItemReaderBuilder<LogCSVDTO>()
                .name("logsCSVReader")
                .linesToSkip(1)
                .delimited()
                .names(new LogCSVDTO().getCSVHeaders())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(LogCSVDTO.class);
                }})
                .strict(false) // False to surpasses file not found exception
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<LogCSVDTO> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<LogCSVDTO>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO logs (id, unix_ts, user_id, event_name) VALUES (:id, :unix_ts, :user_id, :event_name)")
                .dataSource(dataSource)
                .build();
    }

    /**
     * @param jobRepository JobRepository context
     * @param step1 Responsible for writing logs to DB
     * @param step2 Responsible for file cleanup
     * @return Job
     */
    @Bean
    public Job importLogJob(JobRepository jobRepository, Step step1, Step step2) {
        return new JobBuilder("importLogJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .next(step2)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager, JdbcBatchItemWriter<LogCSVDTO> writer) {
        return new StepBuilder("step1", jobRepository)
                .<LogCSVDTO, LogCSVDTO> chunk(1000, transactionManager)
                .reader(multiResourceItemReader(null))
                .writer(writer)
                .build();
    }

    /**
     * @param fileNameSuffix Passed by job launcher from LoggerserviceApplication::class
     * @param jobRepository JobRepository context
     * @param transactionManager Manages transaction properties - Commit, Rollback
     * @return Step - Cleanup
     */
    @JobScope
    @Bean
    public Step step2( @Value("#{jobParameters['fileNameSuffix']}") String fileNameSuffix, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        FileDeletingTasklet task = new FileDeletingTasklet();
        task.setResources(generateResourceNames(Integer.parseInt(fileNameSuffix)));
        return new StepBuilder("step2",jobRepository)
                .tasklet(task, transactionManager)
                .build();
    }
}
