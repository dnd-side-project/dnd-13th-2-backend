package com.eodigo.batch

import com.eodigo.batch.processor.KamisAnnualPriceProcessor
import com.eodigo.batch.writer.KamisAnnualPriceJpaItemWriter
import com.eodigo.domain.product.entity.AnnualNationalPrice
import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.repository.AnnualNationalPriceRepository
import com.eodigo.external.kamis.KamisApiClient
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class KamisAnnualPriceBatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val kamisApiClient: KamisApiClient,
    private val annualNationalPriceRepository: AnnualNationalPriceRepository,
    @Value("\${kamis.api.key}") private val apiKey: String,
    @Value("\${kamis.api.cert-id}") private val certId: String,
) {

    companion object {
        const val JOB_NAME = "kamisAnnualPriceSyncJob"
        private const val STEP_NAME = "kamisAnnualPriceSyncStep"
        private const val CHUNK_SIZE = 50
    }

    @Bean
    fun kamisAnnualPriceSyncJob(): Job {
        return JobBuilder(JOB_NAME, jobRepository).start(kamisAnnualPriceSyncStep()).build()
    }

    @Bean
    fun kamisAnnualPriceSyncStep(): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .chunk<Product, List<AnnualNationalPrice>>(CHUNK_SIZE, transactionManager)
            .reader(kamisAnnualPriceReader())
            .processor(kamisAnnualPriceProcessor())
            .writer(kamisAnnualPriceJpaItemWriter())
            .build()
    }

    @Bean
    fun kamisAnnualPriceReader(): ItemReader<Product> {
        return JpaPagingItemReaderBuilder<Product>()
            .name("kamisAnnualPriceReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString("SELECT p FROM Product p WHERE p.categoryCode != '500' ORDER BY p.id ASC")
            .build()
    }

    @Bean
    fun kamisAnnualPriceProcessor(): ItemProcessor<Product, List<AnnualNationalPrice>> {
        return KamisAnnualPriceProcessor(
            kamisApiClient = kamisApiClient,
            apiKey = apiKey,
            certId = certId,
        )
    }

    @Bean
    fun kamisAnnualPriceJpaItemWriter(): ItemWriter<List<AnnualNationalPrice>> {
        return KamisAnnualPriceJpaItemWriter(annualNationalPriceRepository)
    }
}
