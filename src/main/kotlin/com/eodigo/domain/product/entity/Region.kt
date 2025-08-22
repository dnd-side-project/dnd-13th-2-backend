package com.eodigo.domain.product.entity

import com.eodigo.common.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "region")
class Region(
    @Column(name = "region_name", nullable = false) var regionName: String,
    @Column(name = "region_code", nullable = false, unique = true) var regionCode: String,
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
}
