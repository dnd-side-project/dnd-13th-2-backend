package com.eodigo.domain.product.dto

data class ProductTrendResponse(
    val productName: String,
    val inflationRate: Double,
    val annualData: List<AnnualPriceInfo>,
)

data class AnnualPriceInfo(val year: Int, val averagePrice: Int)
