package umc.stockoneqback.product.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import umc.stockoneqback.product.service.ProductService;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class PassProductBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ProductService productService;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("PassProductJob")
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("PassProductStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== Pass Product Batch Run ===");
                    productService.pushAlarmOfPassProductByOnlineUsers();
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
