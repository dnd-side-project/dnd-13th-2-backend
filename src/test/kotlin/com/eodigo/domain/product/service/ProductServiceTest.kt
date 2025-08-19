package com.eodigo.domain.product.service

import com.eodigo.domain.product.entity.AnnualNationalPrice
import com.eodigo.domain.product.entity.DailyRegionalPrice
import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.entity.Region
import com.eodigo.domain.product.enums.MarketType
import com.eodigo.domain.product.enums.ProductSource
import com.eodigo.domain.product.exception.ProductNotFoundException
import com.eodigo.domain.product.exception.ProductRankingNotFoundException
import com.eodigo.domain.product.exception.ProductTrendNotFoundException
import com.eodigo.domain.product.repository.AnnualNationalPriceRepository
import com.eodigo.domain.product.repository.DailyRegionalPriceRepository
import com.eodigo.domain.product.repository.ProductRepository
import java.time.LocalDate
import java.util.Optional
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.* // BDDMockito를 사용하면 given-when-then과 더 잘 어울립니다.
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class) // 1. JUnit5에서 Mockito를 사용하기 위한 확장
internal class ProductServiceTest {

    @InjectMocks // 2. 테스트 대상 객체. @Mock으로 만든 가짜 객체들이 여기에 주입됩니다.
    private lateinit var productService: ProductService

    @Mock // 3. 가짜(Mock) 객체로 만들 의존성
    private lateinit var productRepository: ProductRepository

    @Mock private lateinit var dailyRegionalPriceRepository: DailyRegionalPriceRepository

    @Mock private lateinit var annualNationalPriceRepository: AnnualNationalPriceRepository

    @Test
    @DisplayName("상품 목록 계층 조회 시, DB의 Product 리스트를 Category-Item-Kind 구조로 정확하게 변환한다")
    fun getProductHierarchy_Success() {

        // given
        val mockProducts =
            listOf(
                // 채소류 > 배추 > 봄, 여름 (3단계)
                Product(
                    name = "봄배추",
                    categoryCode = 200,
                    categoryName = "채소류",
                    itemCode = 201,
                    itemName = "배추",
                    kindCode = 1,
                    kindName = "봄",
                    source = ProductSource.KAMIS,
                ),
                Product(
                    name = "여름배추",
                    categoryCode = 200,
                    categoryName = "채소류",
                    itemCode = 201,
                    itemName = "배추",
                    kindCode = 2,
                    kindName = "여름",
                    source = ProductSource.KAMIS,
                ),

                // 채소류 > 감자 (2단계)
                Product(
                    name = "감자",
                    categoryCode = 200,
                    categoryName = "채소류",
                    itemCode = 202,
                    itemName = "감자",
                    kindCode = null,
                    kindName = null,
                    source = ProductSource.KAMIS,
                ),

                // 축산물 > 돼지고기 > 삼겹살 (3단계)
                Product(
                    name = "삼겹살",
                    categoryCode = 500,
                    categoryName = "축산물",
                    itemCode = 501,
                    itemName = "돼지고기",
                    kindCode = 1,
                    kindName = "삼겹살",
                    source = ProductSource.KAMIS,
                ),
            )

        // 각 Mock Product 객체에 ID를 강제로 주입
        mockProducts.forEachIndexed { index, product ->
            val id = (index + 1).toLong()
            val idField = product::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(product, id)
        }

        given(productRepository.findAllByOrderByIdAsc()).willReturn(mockProducts)

        // when
        val result = productService.getProductHierarchy()

        // then
        // 1. 전체 카테고리 수 검증 (데이터가 있는 카테고리만 반환됨)
        assertThat(result).hasSize(2) // 채소류, 축산물

        // 2. 첫 번째 카테고리 '채소류' 상세 검증
        val vegetableCategory = result.find { it.categoryCode == 200 }
        assertThat(vegetableCategory).isNotNull
        assertThat(vegetableCategory!!.categoryName).isEqualTo("채소류")
        assertThat(vegetableCategory.items).hasSize(2)

        // 2-1. '배추' 품목 (3단계) 검증
        val cabbageItem = vegetableCategory.items.find { it.itemName == "배추" }
        assertThat(cabbageItem).isNotNull
        assertThat(cabbageItem!!.productId).isNull() // 3단계이므로 productId는 null
        assertThat(cabbageItem.kinds).isNotNull
        assertThat(cabbageItem.kinds!!).hasSize(2)
        assertThat(cabbageItem.kinds!!.map { it.kindName }).containsExactlyInAnyOrder("봄", "여름")

        // 2-2. '감자' 품목 (2단계) 검증
        val potatoItem = vegetableCategory.items.find { it.itemName == "감자" }
        assertThat(potatoItem).isNotNull
        assertThat(potatoItem!!.kinds).isNull() // 2단계이므로 kinds는 null
        assertThat(potatoItem.productId).isNotNull
        assertThat(potatoItem.productId).isEqualTo(3)

        // 3. 두 번째 카테고리 '축산물' 상세 검증
        val livestockCategory = result.find { it.categoryCode == 500 }
        assertThat(livestockCategory).isNotNull
        assertThat(livestockCategory!!.items).hasSize(1)

        val porkItem = livestockCategory.items.first()
        assertThat(porkItem.itemName).isEqualTo("돼지고기")
        assertThat(porkItem.kinds!!.first().kindName).isEqualTo("삼겹살")
    }

    @Test
    @DisplayName("지역별 상품 가격 순위 조회 시, DB 데이터를 DTO로 변환하고 순위를 부여하여 성공적으로 반환한다")
    fun getProductRanking_Success() {
        // given
        val productId = 1L
        val mockProduct =
            Product(
                name = "테스트상품",
                categoryCode = 100,
                categoryName = "부류1",
                itemCode = 101,
                itemName = "품목1",
                kindCode = 1,
                kindName = "품종1",
                source = ProductSource.KAMIS,
            )

        // 테스트를 위해 일부러 가격 순서가 뒤섞인 Mock 데이터를 생성
        val mockRegion1 = Region(regionName = "서울", regionCode = 1101)
        val mockRegion2 = Region(regionName = "부산", regionCode = 2100)
        val mockRegion3 = Region(regionName = "대구", regionCode = 2200)

        val mockPrices =
            listOf(
                DailyRegionalPrice(
                    product = mockProduct,
                    region = mockRegion1,
                    price = 1200,
                    surveyDate = LocalDate.now(),
                    marketType = MarketType.RETAIL,
                ),
                DailyRegionalPrice(
                    product = mockProduct,
                    region = mockRegion2,
                    price = 1000,
                    surveyDate = LocalDate.now(),
                    marketType = MarketType.RETAIL,
                ),
                DailyRegionalPrice(
                    product = mockProduct,
                    region = mockRegion3,
                    price = 1500,
                    surveyDate = LocalDate.now(),
                    marketType = MarketType.RETAIL,
                ),
            )

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct))
        given(dailyRegionalPriceRepository.findLatestPricesByProductId(productId))
            .willReturn(mockPrices)

        // when
        val result = productService.getProductRanking(productId)

        // then
        assertThat(result.productId).isEqualTo(productId)
        assertThat(result.productName).isEqualTo("테스트상품")
        assertThat(result.ranking).hasSize(3)

        // 정렬과 순위 부여가 올바르게 되었는지 상세 검증
        assertThat(result.ranking[0].rank).isEqualTo(1)
        assertThat(result.ranking[0].price).isEqualTo(1000)
        assertThat(result.ranking[0].regionName).isEqualTo("부산")

        assertThat(result.ranking[1].rank).isEqualTo(2)
        assertThat(result.ranking[1].price).isEqualTo(1200)
        assertThat(result.ranking[1].regionName).isEqualTo("서울")

        assertThat(result.ranking[2].rank).isEqualTo(3)
        assertThat(result.ranking[2].price).isEqualTo(1500)
        assertThat(result.ranking[2].regionName).isEqualTo("대구")
    }

    @Test
    @DisplayName("가격 추이 조회 시, 10년이 넘는 데이터는 필터링하고 물가 상승률을 정확하게 계산한다")
    fun getProductTrend_FiltersAndCalculatesCorrectly() {
        // given
        val productId = 1L
        val mockProduct =
            Product(
                name = "테스트상품",
                categoryCode = 100,
                categoryName = "부류1",
                itemCode = 101,
                itemName = "품목1",
                kindCode = 1,
                kindName = "품종1",
                source = ProductSource.KAMIS,
            )

        // 11년 치(2015 ~ 2025)의 가짜 AnnualNationalPrice 데이터를 생성
        // 2016년 가격: 10000, 2025년 가격: 15000 -> 상승률 50.0% 예상
        val mockPrices =
            (2015..2025)
                .map { year ->
                    val price = if (year == 2016) 10000 else if (year == 2025) 15000 else 12000
                    AnnualNationalPrice(
                        product = mockProduct,
                        price = price,
                        surveyYear = year,
                        marketType = MarketType.RETAIL,
                    )
                }
                .sortedByDescending { it.surveyYear } // Repository의 반환값처럼 연도 내림차순으로 정렬

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct))
        given(annualNationalPriceRepository.findByProductIdOrderBySurveyYearDesc(productId))
            .willReturn(mockPrices)

        // when
        val result = productService.getProductTrend(productId)

        // then
        // 1. "최근 10년" 필터링 검증
        assertThat(result.annualData).hasSize(10) // 11개가 아닌 10개만 포함되어야 함
        assertThat(result.annualData.map { it.year }).doesNotContain(2015) // 2015년 데이터는 없어야 함

        // 2. DTO의 시작/종료 연도 검증
        assertThat(result.startYear).isEqualTo(2016)
        assertThat(result.endYear).isEqualTo(2025)

        // 3. 물가 상승률 계산 검증: ((15000 - 10000) / 10000) * 100 = 50.0
        assertThat(result.inflationRate).isEqualTo(50.0)
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 조회 시, ProductNotFoundException 예외를 던진다")
    fun findProduct_Throws_ProductNotFoundException_WhenProductNotFound() {
        // given
        val nonExistingId = 999L
        given(productRepository.findById(nonExistingId)).willReturn(Optional.empty())

        // when & then
        assertThatThrownBy { productService.getProductRanking(nonExistingId) }
            .isInstanceOf(ProductNotFoundException::class.java)
    }

    @Test
    @DisplayName("가격 랭킹 조회 시 최신 가격 데이터가 없으면, ProductRankingNotFoundException 예외를 던진다")
    fun getProductRanking_Throws_ProductRankingNotFoundException_WhenNoPriceData() {
        // given
        val productId = 1L
        val mockProduct =
            Product(
                name = "데이터없는상품",
                categoryCode = 100,
                categoryName = "category1",
                itemCode = 111,
                itemName = "item1",
                kindCode = 1,
                kindName = "kind",
                source = ProductSource.KAMIS,
            )

        // 상품은 존재한다고 가정
        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct))

        // 가격 데이터는 비어있는 리스트를 반환하도록 설정
        given(dailyRegionalPriceRepository.findLatestPricesByProductId(productId))
            .willReturn(emptyList())

        // when & then
        assertThatThrownBy { productService.getProductRanking(productId) }
            .isInstanceOf(ProductRankingNotFoundException::class.java)
    }

    @Test
    @DisplayName("가격 추이 조회 시 연도별 가격 데이터가 없으면, ProductTrendNotFoundException 예외를 던진다")
    fun getProductTrend_Throws_ProductTrendNotFoundException_WhenNoPriceData() {
        // given
        val productId = 1L
        val mockProduct =
            Product(
                name = "데이터없는상품",
                categoryCode = 100,
                categoryName = "category1",
                itemCode = 111,
                itemName = "item1",
                kindCode = 1,
                kindName = "kind",
                source = ProductSource.KAMIS,
            )

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct))

        // 연도별 가격 데이터는 비어있는 리스트를 반환하도록 설정
        given(annualNationalPriceRepository.findByProductIdOrderBySurveyYearDesc(productId))
            .willReturn(emptyList())

        // when & then
        assertThatThrownBy { productService.getProductTrend(productId) }
            .isInstanceOf(ProductTrendNotFoundException::class.java)
    }
}
