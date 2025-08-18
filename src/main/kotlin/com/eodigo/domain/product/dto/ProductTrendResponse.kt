package com.eodigo.domain.product.dto

data class ProductTrendResponse(
    val productId: Long,
    val productName: String,
    val startYear: Int,
    val endYear: Int,
    val inflationRate: Double,
    val annualData: List<AnnualPriceInfo>,
)

data class AnnualPriceInfo(val year: Int, val averagePrice: Int)
