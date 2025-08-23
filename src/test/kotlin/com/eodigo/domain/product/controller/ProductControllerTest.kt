package com.eodigo.domain.product.controller

import com.eodigo.common.exception.ErrorCode
import com.eodigo.common.exception.GlobalExceptionHandler
import com.eodigo.domain.product.dto.AnnualPriceInfo
import com.eodigo.domain.product.dto.CategoryInfo
import com.eodigo.domain.product.dto.ProductRankingResponse
import com.eodigo.domain.product.dto.ProductTrendResponse
import com.eodigo.domain.product.dto.RegionalPriceInfo
import com.eodigo.domain.product.exception.ProductNotFoundException
import com.eodigo.domain.product.exception.ProductRankingNotFoundException
import com.eodigo.domain.product.service.ProductService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
internal class ProductControllerTest {

    private lateinit var mockMvc: MockMvc

    @InjectMocks private lateinit var productController: ProductController

    @Mock private lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        mockMvc =
            MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    @Test
    @DisplayName("GET /api/v1/products/hierarchy - 성공 시, 상품 목록 계층 DTO와 200 OK를 반환한다")
    fun getProductHierarchy_Success() {
        // given
        val mockResponse =
            listOf(CategoryInfo(categoryName = "채소류", categoryCode = "200", items = emptyList()))
        given(productService.getProductHierarchy()).willReturn(mockResponse)

        // when & then
        mockMvc
            .perform(get("/api/v1/products/hierarchy").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].categoryName").value("채소류"))
            .andDo(print())
    }

    @Test
    @DisplayName("GET /api/v1/products/{productId}/trends - 성공 시, 상품 가격 추이 DTO와 200 OK를 반환한다")
    fun getProductTrend_Success() {
        // given
        val productId = 1L
        val mockResponse =
            ProductTrendResponse(
                productName = "테스트상품",
                inflationRate = 50.0,
                annualData = listOf(AnnualPriceInfo(2025, 15000)),
            )
        given(productService.getProductTrend(productId)).willReturn(mockResponse)

        // when & then
        mockMvc
            .perform(
                get("/api/v1/products/{productId}/trends", productId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.productName").value("테스트상품"))
            .andExpect(jsonPath("$.inflationRate").value(50.0))
            .andExpect(jsonPath("$.annualData[0].year").value(2025))
    }

    @Test
    @DisplayName("GET /api/v1/products/{productId}/rankings - 성공 시, 상품 가격 랭킹 DTO와 200 OK를 반환한다")
    fun getProductRanking_Success() {
        // given
        val productId = 1L
        val mockResponse =
            ProductRankingResponse(
                productName = "테스트상품",
                ranking = listOf(RegionalPriceInfo("서울", 3000)),
            )
        given(productService.getProductRanking(productId)).willReturn(mockResponse)

        // when & then
        mockMvc
            .perform(
                get("/api/v1/products/{productId}/rankings", productId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.productName").value("테스트상품"))
            .andExpect(jsonPath("$.ranking[0].regionName").value("서울"))
    }

    @Test
    @DisplayName(
        "GET /api/v1/products/{productId}/trends - 존재하지 않는 상품 ID로 요청 시, 404 Not Found를 반환한다"
    )
    fun getProductTrend_Failure_WhenProductNotFound() {
        // given
        val nonExistingId = 999L
        given(productService.getProductTrend(nonExistingId)).willThrow(ProductNotFoundException())

        // when & then
        mockMvc
            .perform(get("/api/v1/products/{productId}/trends", nonExistingId))
            .andExpect(status().isNotFound) // HTTP 404 검증
            .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_NOT_FOUND.code))
    }

    @Test
    @DisplayName(
        "GET /api/v1/products/{productId}/rankings - 가격 데이터가 없을 때 요청 시, 404 Not Found를 반환한다"
    )
    fun getProductRanking_Failure_WhenPriceDataNotFound() {
        // given
        val productIdWithNoPrice = 2L
        given(productService.getProductRanking(productIdWithNoPrice))
            .willThrow(ProductRankingNotFoundException())

        // when & then
        mockMvc
            .perform(get("/api/v1/products/{productId}/rankings", productIdWithNoPrice))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value(ErrorCode.PRODUCT_RANKING_NOT_FOUND.code))
    }
}
