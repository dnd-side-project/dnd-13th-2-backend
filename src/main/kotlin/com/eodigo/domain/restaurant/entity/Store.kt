package com.eodigo.domain.restaurant.entity

import com.eodigo.common.entity.BaseTimeEntity
import com.eodigo.domain.restaurant.enums.StoreCategory
import jakarta.persistence.*

@Entity
@Table(name = "store")
class Store(
        @Column(name = "name", nullable = false)
        val name: String,                              // 매장명

        @Enumerated(EnumType.STRING)
        @Column(name = "category", nullable = false)
        val category: StoreCategory,                   // 카테고리 (CAFE, RESTAURANT)

        @Column(name = "address", nullable = false)
        val address: String,                           // 주소

        @Column(name = "latitude", nullable = false)
        val latitude: Double,                          // 위도

        @Column(name = "longitude", nullable = false)
        val longitude: Double,                         // 경도

        @Column(name = "img_url", nullable = true)
        val imgUrl: String                             // 이미지 url

) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}