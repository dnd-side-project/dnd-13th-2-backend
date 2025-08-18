package com.eodigo.domain.product.entity

import com.eodigo.common.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "annual_national_price")
class AnnualNationalPrice(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,
    @Column(name = "price") val price: Int,
    @Column(name = "survey_year") val surveyYear: Int,
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
}
