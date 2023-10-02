package com.fastcampus.pass.job.pass;

import com.fastcampus.pass.repository.pass.Pass;
import com.fastcampus.pass.repository.pass.PassStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ExpirePassesJobConfig {

    private final int CHUNK_SIZE = 5;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory em;

    @Bean
    public Job expirePassesJob() {
        return this.jobBuilderFactory.get("expirePassesJob")
                .start(expirePassesStep())
                .build();
    }

    @Bean
    public Step expirePassesStep() {
        return this.stepBuilderFactory.get("expirePassesStep")
                .<Pass, Pass>chunk(CHUNK_SIZE)
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassesItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Pass> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<Pass>()
                .name("expirePassesItemReader")
                .entityManagerFactory(em)
                .queryString("select p from Pass p where p.status = :status and p.endedAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<Pass, Pass> expirePassesItemProcessor() {
        return pass -> {
            pass.setStatus(PassStatus.EXPIRED);
            pass.setExpiredAt(LocalDateTime.now());
            return pass;
        };
    }

    @Bean
    public JpaItemWriter<Pass> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<Pass>()
                .entityManagerFactory(em)
                .build();
    }

}
