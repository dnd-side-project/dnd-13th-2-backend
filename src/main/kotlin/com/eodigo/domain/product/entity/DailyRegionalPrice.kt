package com.eodigo.domain.product.entity

import com.eodigo.common.entity.BaseTimeEntity
import com.eodigo.domain.product.enums.MarketType
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "daily_regional_price")
class DailyRegionalPrice(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    val region: Region,
    @Column(name = "survey_date", nullable = false) val surveyDate: LocalDate,
    @Column(name = "price", nullable = false) val price: Int,
    @Enumerated(EnumType.STRING)
    @Column(name = "market_type", nullable = false)
    val marketType: MarketType = MarketType.RETAIL,
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
}
