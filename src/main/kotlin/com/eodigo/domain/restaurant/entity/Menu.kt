package com.eodigo.domain.restaurant.entity

import com.eodigo.common.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "menu")
class Menu(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "store_id", nullable = false)
        val store: Store,                              // 매장 FK

        @Column(name = "name", nullable = false)
        val name: String,                              // 메뉴명

        @Column(name = "price", nullable = false)
        val price: Int                                 // 가격

) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}