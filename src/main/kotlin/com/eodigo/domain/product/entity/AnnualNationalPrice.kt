package com.eodigo.domain.product.entity

import com.eodigo.common.entity.BaseTimeEntity
import com.eodigo.domain.product.enums.MarketType
import jakarta.persistence.*

@Entity
@Table(name = "annual_national_price")
class AnnualNationalPrice(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,
    @Column(name = "survey_year", nullable = false) val surveyYear: Int,
    @Column(name = "price", nullable = false) val price: Int,
    @Enumerated(EnumType.STRING) @Column(name = "market_type") val marketType: MarketType?,
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
}
