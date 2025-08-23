package com.eodigo.domain.product.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class ProductHierarchyResponse(val categories: List<CategoryInfo>)

data class CategoryInfo(val categoryName: String, val categoryCode: String, val items: List<ItemInfo>)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ItemInfo(val itemName: String, val productId: Long?, val kinds: List<KindInfo>?)

data class KindInfo(val productId: Long, val kindName: String)
