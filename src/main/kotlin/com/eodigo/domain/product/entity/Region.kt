package com.eodigo.domain.product.entity

import com.eodigo.common.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "region")
class Region(
    @Column(name = "name", nullable = false) var name: String,
    @Column(name = "code", nullable = false, unique = true) var code: String,
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
}
