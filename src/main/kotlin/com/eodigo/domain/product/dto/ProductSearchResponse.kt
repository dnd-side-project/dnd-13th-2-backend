package com.eodigo.domain.product.dto

import com.eodigo.domain.product.entity.Product
import io.swagger.v3.oas.annotations.media.Schema

data class ProductSearchResponse(
    @Schema(description = "상품의 ID", example = "101") val productId: Long,
    @Schema(description = "상품의 이름", example = "대파") val name: String,
    @Schema(description = "상품의 품목명", example = "파") val itemName: String,
) {
    companion object {
        fun from(product: Product): ProductSearchResponse {
            return ProductSearchResponse(
                productId =
                    requireNotNull(product.id) { "ProductSearchResponse: product.id가 null입니다." },
                name = product.name,
                itemName = product.itemName,
            )
        }
    }
}
