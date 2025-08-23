package com.eodigo.domain.product.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "가격 랭킹 조회 응답 DTO")
data class ProductRankingResponse(
    @Schema(description = "조회 대상 상품의 이름", example = "수박") val productName: String,
    @Schema(description = "지역별 가격 순위 목록 (가격 오름차순)") val ranking: List<RegionalPriceInfo>,
)

@Schema(description = "개별 지역의 가격 정보")
data class RegionalPriceInfo(
    @Schema(description = "지역 이름", example = "서울") val regionName: String,
    @Schema(description = "해당 지역의 가격", example = "3100") val price: Int,
)
