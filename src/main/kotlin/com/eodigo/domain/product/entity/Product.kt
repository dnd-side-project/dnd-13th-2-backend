package com.eodigo.domain.product.entity

import com.eodigo.common.entity.BaseTimeEntity
import com.eodigo.domain.product.enums.ProductSource
import jakarta.persistence.*

@Entity
@Table(name = "product")
class Product(
    @Column(name = "name", nullable = false) val name: String,
    @Column(name = "category_code", nullable = false) val categoryCode: Int,
    @Column(name = "category_name", nullable = false) val categoryName: String,
    @Column(name = "item_code", nullable = false) val itemCode: Int,
    @Column(name = "item_name", nullable = false) val itemName: String,
    @Column(name = "kind_code") val kindCode: Int?,
    @Column(name = "kind_name") val kindName: String?,
    @Column(name = "source", nullable = false) val source: ProductSource,
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
}
