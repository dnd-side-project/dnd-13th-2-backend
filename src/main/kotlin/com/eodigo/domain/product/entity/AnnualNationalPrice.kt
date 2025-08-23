package com.eodigo.domain.product.entity

import com.eodigo.common.entity.BaseTimeEntity
import com.eodigo.domain.product.enums.MarketType
import jakarta.persistence.*

@Entity
@Table(
    name = "annual_national_price",
    uniqueConstraints =
        [
            UniqueConstraint(
                name = "uk_annual_price_product_year_market",
                columnNames = ["product_id", "survey_year", "market_type"],
            )
        ],
)
class AnnualNationalPrice(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,
    @Column(name = "survey_year", nullable = false) val surveyYear: Int,
    @Column(name = "price", nullable = false) var price: Int,
    @Enumerated(EnumType.STRING)
    @Column(name = "market_type", nullable = false)
    val marketType: MarketType = MarketType.RETAIL,
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null

    fun updatePrice(newPrice: Int) {
        require(newPrice >= 0) { "price는 음수가 될 수 없습니다." }
        this.price = newPrice
    }
}
