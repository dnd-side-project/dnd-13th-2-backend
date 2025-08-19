package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.DailyRegionalPrice
import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.entity.Region
import com.eodigo.domain.product.enums.MarketType
import com.eodigo.domain.product.enums.ProductSource
import java.time.LocalDate
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
internal class DailyRegionalPriceRepositoryTest {

    @Autowired private lateinit var dailyRegionalPriceRepository: DailyRegionalPriceRepository

    @Autowired private lateinit var entityManager: TestEntityManager

    private lateinit var product1: Product
    private lateinit var region1: Region
    private lateinit var region2: Region

    @BeforeEach
    fun setUp() {
        // given
        product1 =
            Product(
                name = "테스트상품1",
                categoryCode = 100,
                categoryName = "테스트부류",
                itemCode = 101,
                itemName = "테스트품목",
                kindCode = null,
                kindName = null,
                source = ProductSource.KAMIS,
            )
        entityManager.persist(product1)

        region1 = Region(regionName = "테스트지역1", regionCode = 1001)
        entityManager.persist(region1)

        region2 = Region(regionName = "테스트지역2", regionCode = 1002)
        entityManager.persist(region2)
    }

    @Test
    @DisplayName("특정 상품의 최신 일자 지역별 가격 목록을 성공적으로 조회한다")
    fun findLatestPricesByProductId_Success() {
        // given
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        // 최신 날짜 데이터 2개
        val price1 =
            DailyRegionalPrice(
                product = product1,
                region = region1,
                price = 1000,
                surveyDate = today,
                marketType = MarketType.RETAIL,
            )
        entityManager.persist(price1)
        val price2 =
            DailyRegionalPrice(
                product = product1,
                region = region2,
                price = 1200,
                surveyDate = today,
                marketType = MarketType.RETAIL,
            )
        entityManager.persist(price2)

        // 이전 날짜 데이터 1개 - 조회되면 안 됨
        val oldPrice =
            DailyRegionalPrice(
                product = product1,
                region = region1,
                price = 900,
                surveyDate = yesterday,
                marketType = MarketType.RETAIL,
            )
        entityManager.persist(oldPrice)

        // 다른 상품에 대한 데이터 - 조회되면 안 됨
        val otherProduct =
            Product(
                name = "다른상품",
                categoryCode = 200,
                categoryName = "다른부류",
                itemCode = 201,
                itemName = "다른품목",
                kindCode = null,
                kindName = null,
                source = ProductSource.KAMIS,
            )
        entityManager.persist(otherProduct)
        val otherPrice =
            DailyRegionalPrice(
                product = otherProduct,
                region = region1,
                price = 2000,
                surveyDate = today,
                marketType = MarketType.RETAIL,
            )
        entityManager.persist(otherPrice)

        // when
        val latestPrices = dailyRegionalPriceRepository.findLatestPricesByProductId(product1.id!!)

        // then
        assertThat(latestPrices).hasSize(2)
        assertThat(latestPrices).allSatisfy {
            assertThat(it.surveyDate).isEqualTo(today)
            assertThat(it.product.id).isEqualTo(product1.id)
        }

        assertThat(latestPrices.map { it.region.regionName })
            .containsExactlyInAnyOrder("테스트지역1", "테스트지역2")
    }
}
