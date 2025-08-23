package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.AnnualNationalPrice
import com.eodigo.domain.product.enums.MarketType
import org.springframework.data.jpa.repository.JpaRepository

interface AnnualNationalPriceRepository : JpaRepository<AnnualNationalPrice, Long> {
    fun findByProductIdOrderBySurveyYearAsc(productId: Long): List<AnnualNationalPrice>

    fun findByProductIdAndSurveyYearAndMarketType(
        productId: Long,
        surveyYear: Int,
        marketType: MarketType,
    ): AnnualNationalPrice?
}
