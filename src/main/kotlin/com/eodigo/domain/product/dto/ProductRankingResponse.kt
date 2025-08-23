package com.eodigo.domain.product.dto

data class ProductRankingResponse(val productName: String, val ranking: List<RegionalPriceInfo>)

data class RegionalPriceInfo(val regionName: String, val price: Int)
