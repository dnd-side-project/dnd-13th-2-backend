package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.DailyRegionalPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DailyRegionalPriceRepository : JpaRepository<DailyRegionalPrice, Long> {

    @Query(
        """
        SELECT drp FROM DailyRegionalPrice drp
        JOIN FETCH drp.region
        WHERE drp.product.id = :productId
        AND drp.surveyDate = (SELECT MAX(sub_drp.surveyDate) FROM DailyRegionalPrice sub_drp WHERE sub_drp.product.id = :productId)
    """
    )
    fun findLatestPricesByProductId(productId: Long): List<DailyRegionalPrice>
}
