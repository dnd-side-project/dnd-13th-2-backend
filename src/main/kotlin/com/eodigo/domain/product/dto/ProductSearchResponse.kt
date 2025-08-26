package com.eodigo.domain.product.dto

import com.eodigo.domain.product.entity.Product

data class ProductSearchResponse(val productId: Long, val name: String, val itemName: String) {
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
