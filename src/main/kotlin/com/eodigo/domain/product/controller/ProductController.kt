package com.eodigo.domain.product.controller

import com.eodigo.common.exception.ErrorResponse
import com.eodigo.domain.product.dto.CategoryInfo
import com.eodigo.domain.product.dto.ProductRankingResponse
import com.eodigo.domain.product.dto.ProductSearchResponse
import com.eodigo.domain.product.dto.ProductTrendResponse
import com.eodigo.domain.product.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "물가 API", description = "물가 정보 관련 API")
@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val productService: ProductService) {

    @Operation(summary = "전체 상품 목록 조회", description = "전체 상품 목록을 계층 구조로 조회합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "상품 목록 계층 조회 성공")])
    @GetMapping("/hierarchy")
    fun getProductHierarchy(): ResponseEntity<List<CategoryInfo>> {
        val hierarchyData = productService.getProductHierarchy()
        return ResponseEntity.ok(hierarchyData)
    }

    @Operation(summary = "가격 랭킹 조회", description = "특정 상품의 최신 지역별 가격을 저렴한 순으로 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "가격 랭킹 조회 성공"),
                ApiResponse(
                    responseCode = "404",
                    description = "해당 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Product Not Found",
                                            description = "존재하지 않는 상품 ID",
                                            value =
                                                "{\"status\": 404, \"code\": \"P001\", \"message\": \"상품을 찾을 수 없습니다.\"}",
                                        ),
                                        ExampleObject(
                                            name = "Product Ranking Not Found",
                                            description = "가격 랭킹 정보 없음",
                                            value =
                                                "{\"status\": 404, \"code\": \"P002\", \"message\": \"가격 랭킹을 찾을 수 없습니다.\"}",
                                        ),
                                    ],
                            )
                        ],
                ),
            ]
    )
    @GetMapping("/{productId}/rankings")
    fun getProductRanking(@PathVariable productId: Long): ResponseEntity<ProductRankingResponse> {
        val rankingData = productService.getProductRanking(productId)
        return ResponseEntity.ok(rankingData)
    }

    @Operation(summary = "상품 가격 추이 조회", description = "특정 상품의 최근 10년간 연도별 전국 평균 가격 추이를 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "가격 추이 조회 성공"),
                ApiResponse(
                    responseCode = "404",
                    description = "해당 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Product Not Found",
                                            description = "존재하지 않는 상품 ID",
                                            value =
                                                "{\"status\": 404, \"code\": \"P001\", \"message\": \"상품을 찾을 수 없습니다.\"}",
                                        ),
                                        ExampleObject(
                                            name = "Product Trend Not Found",
                                            description = "가격 랭킹 정보 없음",
                                            value =
                                                "{\"status\": 404, \"code\": \"P003\", \"message\": \"가격 추이를 찾을 수 없습니다.\"}",
                                        ),
                                    ],
                            )
                        ],
                ),
            ]
    )
    @GetMapping("/{productId}/trends")
    fun getProductTrend(@PathVariable productId: Long): ResponseEntity<ProductTrendResponse> {
        val trendData = productService.getProductTrend(productId)
        return ResponseEntity.ok(trendData)
    }

    @Operation(summary = "상품 검색", description = "키워드가 포함된 상품 목록을 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "상품 검색 성공. 검색 결과가 없거나, 검색어가 비어있는 경우 빈 배열을 반환합니다.",
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "필수 파라미터가 누락된 경우",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Invalid Input Value",
                                            description = "쿼리 파라미터 'keyword'가 누락됨",
                                            value =
                                                "{\"status\": 400, \"code\": \"C001\", \"message\": \"필수 파라미터 'keyword'(이)가 누락되었습니다.\"}",
                                        )
                                    ],
                            )
                        ],
                ),
            ]
    )
    @GetMapping("/search")
    fun searchProducts(
        @RequestParam("keyword") keyword: String
    ): ResponseEntity<List<ProductSearchResponse>> {
        val searchResult = productService.searchProducts(keyword)
        return ResponseEntity.ok(searchResult)
    }
}
