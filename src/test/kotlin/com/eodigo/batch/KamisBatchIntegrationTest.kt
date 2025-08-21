package com.eodigo.batch

//import com.eodigo.domain.product.entity.Product
//import com.eodigo.domain.product.entity.Region
//import com.eodigo.domain.product.enums.ProductSource
//import com.eodigo.domain.product.repository.DailyRegionalPriceRepository
//import com.eodigo.domain.product.repository.ProductRepository
//import com.eodigo.domain.product.repository.RegionRepository
//import com.eodigo.util.DotenvInitializer
//import java.time.LocalDate
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.springframework.batch.core.ExitStatus
//import org.springframework.batch.core.JobParametersBuilder
//import org.springframework.batch.test.JobLauncherTestUtils
//import org.springframework.batch.test.context.SpringBatchTest
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.context.jdbc.Sql
//
//@SpringBootTest
//@SpringBatchTest
//@Sql("/schema-h2.sql")
//@ContextConfiguration(initializers = [DotenvInitializer::class])
//@ActiveProfiles("test")
//internal class KamisBatchIntegrationTest {
//
//    @Autowired private lateinit var jobLauncherTestUtils: JobLauncherTestUtils
//
//    @Autowired private lateinit var dailyRegionalPriceRepository: DailyRegionalPriceRepository
//    @Autowired private lateinit var productRepository: ProductRepository
//    @Autowired private lateinit var regionRepository: RegionRepository
//
//    @BeforeEach
//    fun setUp() {
//        regionRepository.saveAll(
//            listOf(
//                Region(regionName = "서울", regionCode = 1101),
//                Region(regionName = "부산", regionCode = 2100),
//            )
//        )
//
//        productRepository.saveAll(
//            listOf(
//                Product(
//                    name = "쌀 20kg",
//                    categoryCode = 100,
//                    categoryName = "식량작물",
//                    itemCode = 111,
//                    itemName = "쌀",
//                    kindCode = 1,
//                    kindName = "20kg",
//                    source = ProductSource.KAMIS,
//                ),
//                Product(
//                    name = "고랭지 배추",
//                    categoryCode = 200,
//                    categoryName = "채소류",
//                    itemCode = 211,
//                    itemName = "배추",
//                    kindCode = 2,
//                    kindName = "여름",
//                    source = ProductSource.KAMIS,
//                ),
//            )
//        )
//    }
//
//    @AfterEach
//    fun tearDown() {
//        dailyRegionalPriceRepository.deleteAllInBatch()
//        productRepository.deleteAllInBatch()
//        regionRepository.deleteAllInBatch()
//    }
//
//    @Test
//    @DisplayName("kamisDailyPriceSyncJob을 실행하면, API 데이터를 읽어 DB에 성공적으로 저장한다")
//    fun kamisDailyPriceSyncJob_Success() {
//        val jobParameters =
//            JobParametersBuilder().addLocalDate("run.date", LocalDate.now()).toJobParameters()
//
//        // when
//        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
//
//        // then
//        assertThat(jobExecution.exitStatus).isEqualTo(ExitStatus.COMPLETED)
//        val results = dailyRegionalPriceRepository.findAll()
//        assertThat(results).isNotEmpty
//        assertThat(results[0].surveyDate).isEqualTo(LocalDate.now())
//
//        assertThat(results[0].product).isNotNull
//        assertThat(results[0].region).isNotNull
//    }
//}
