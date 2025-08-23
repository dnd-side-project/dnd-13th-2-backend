package com.eodigo.domain.product.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "가격 추이 조회 응답 DTO")
data class ProductTrendResponse(
    @Schema(description = "조회 대상 상품의 이름", example = "수박") val productName: String,
    @Schema(description = "조회 기간 전체의 물가 상승률 (%)", example = "35.5") val inflationRate: Double,
    @Schema(description = "최근 10년간의 연도별 전국 평균 가격 목록 (연도 오름차순)")
    val annualData: List<AnnualPriceInfo>,
)

@Schema(description = "특정 연도의 전국 평균 가격 정보")
data class AnnualPriceInfo(
    @Schema(description = "조사 연도", example = "2024") val year: Int,
    @Schema(description = "해당 연도의 전국 평균 가격", example = "3800") val averagePrice: Int,
)
