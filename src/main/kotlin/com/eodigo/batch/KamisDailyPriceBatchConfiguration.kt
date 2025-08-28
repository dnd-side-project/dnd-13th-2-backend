package com.eodigo.batch

import com.eodigo.batch.dto.KamisDailyPriceApiData
import com.eodigo.batch.processor.KamisDailyPriceProcessor
import com.eodigo.batch.reader.KamisDailyPriceApiReader
import com.eodigo.domain.product.entity.DailyRegionalPrice
import com.eodigo.domain.product.repository.ProductRepository
import com.eodigo.domain.product.repository.RegionRepository
import com.eodigo.external.kamis.KamisApiClient
import jakarta.persistence.EntityManagerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.reactive.function.client.WebClientRequestException

@Configuration
class KamisDailyPriceBatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val kamisApiClient: KamisApiClient,
    private val productRepository: ProductRepository,
    private val regionRepository: RegionRepository,
    private val entityManagerFactory: EntityManagerFactory,
    @Value("\${kamis.api.key}") private val apiKey: String,
    @Value("\${kamis.api.cert-id}") private val certId: String,
) {

    companion object {
        const val JOB_NAME = "kamisDailyPriceSyncJob"
        const val STEP_NAME = "kamisDailyPriceSyncStep"
        const val CHUNK_SIZE = 100
    }

    @Bean
    fun kamisDailyPriceSyncJob(): Job {
        return JobBuilder(JOB_NAME, jobRepository).start(kamisDailyPriceSyncStep()).build()
    }

    @Bean
    fun kamisDailyPriceSyncStep(): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .chunk<KamisDailyPriceApiData, DailyRegionalPrice>(CHUNK_SIZE, transactionManager)
            .reader(kamisDailyPriceApiReader(null))
            .processor(kamisDailyPriceProcessor(null))
            .writer(kamisDailyPriceJpaWriter())
            .faultTolerant()
            .retryLimit(3)
            .retry(WebClientRequestException::class.java)
            .build()
    }

    @Bean
    @StepScope
    fun kamisDailyPriceApiReader(
        @Value("#{jobParameters['surveyDate']}") surveyDateString: String?
    ): ItemReader<KamisDailyPriceApiData> {
        val surveyDate =
            surveyDateString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
                ?: throw IllegalArgumentException("surveyDate job parameter is required")

        val categoryCodes = listOf("100", "200", "300", "400", "500", "600")
        val regionCodes =
            listOf(
                "1101",
                "2100",
                "2200",
                "2300",
                "2401",
                "2501",
                "2601",
                "3111",
                "3214",
                "3211",
                "3311",
                "3511",
                "3711",
                "3911",
                "3113",
                "3613",
                "3714",
                "3814",
                "3145",
                "2701",
                "3112",
                "3138",
                "3411",
                "3818",
            )

        return KamisDailyPriceApiReader(
            kamisApiClient = kamisApiClient,
            apiKey = apiKey,
            certId = certId,
            categoryCodes = categoryCodes,
            regionCodes = regionCodes,
            surveyDate = surveyDate,
        )
    }

    @Bean
    @StepScope
    fun kamisDailyPriceProcessor(
        @Value("#{jobParameters['surveyDate']}") surveyDateString: String?
    ): ItemProcessor<KamisDailyPriceApiData, DailyRegionalPrice> {
        val surveyDate =
            surveyDateString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
                ?: throw IllegalArgumentException("surveyDate job parameter is required")

        return KamisDailyPriceProcessor(
            productRepository = productRepository,
            regionRepository = regionRepository,
            surveyDate = surveyDate,
        )
    }

    @Bean
    fun kamisDailyPriceJpaWriter(): JpaItemWriter<DailyRegionalPrice> {
        return JpaItemWriterBuilder<DailyRegionalPrice>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }
}
