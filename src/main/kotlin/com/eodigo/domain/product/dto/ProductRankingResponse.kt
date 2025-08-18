package com.eodigo.domain.product.dto

import java.time.LocalDate

data class ProductRankingResponse(
    val productId: Long,
    val productName: String,
    val surveyDate: LocalDate,
    val ranking: List<RegionalPriceInfo>,
)

data class RegionalPriceInfo(val rank: Int, val regionName: String, val price: Int)
