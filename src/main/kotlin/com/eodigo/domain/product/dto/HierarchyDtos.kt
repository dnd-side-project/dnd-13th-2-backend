package com.eodigo.domain.product.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 계층 구조의 최상위인 '부류' 정보")
data class CategoryInfo(
    @Schema(description = "부류 이름", example = "채소류") val categoryName: String,
    @Schema(description = "부류 코드", example = "\"200\"") val categoryCode: String,
    @Schema(description = "해당 부류에 속한 '품목' 정보 목록") val items: List<ItemInfo>,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "상품 계층 구조의 중간인 '품목' 정보")
data class ItemInfo(
    @Schema(description = "품목 이름", example = "배추") val itemName: String,
    @Schema(
        description = "품종이 없는 2단계 계층 상품의 경우, 직접 선택 가능한 상품 ID (3단계 계층일 경우 null)",
        nullable = true,
        example = "110",
    )
    val productId: Long?,
    @Schema(description = "해당 품목에 속한 '품종' 정보 목록 (2단계 계층일 경우 null)", nullable = true)
    val kinds: List<KindInfo>?,
)

@Schema(description = "상품 계층 구조의 최하위인 '품종' 정보")
data class KindInfo(
    @Schema(description = "사용자가 최종 선택하는 상품의 고유 ID", example = "42") val productId: Long,
    @Schema(description = "품종 이름", example = "봄") val kindName: String,
)
