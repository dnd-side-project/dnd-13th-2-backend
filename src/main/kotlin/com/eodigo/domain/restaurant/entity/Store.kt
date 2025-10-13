package com.eodigo.domain.restaurant.entity

import com.eodigo.common.entity.BaseTimeEntity
import com.eodigo.domain.restaurant.enums.StoreCategory
import jakarta.persistence.*
import org.locationtech.jts.geom.Point // Point 타입 import

@Entity
@Table(name = "store_backup")
class Store(
        @Column(name = "name", nullable = false)
        val name: String, // 매장명
        @Enumerated(EnumType.STRING)
        @Column(name = "category", nullable = false)
        val category: StoreCategory, // 카테고리 (CAFE, RESTAURANT)
        @Column(name = "address", nullable = false)
        val address: String, // 주소
        @Column(name = "location", nullable = false, columnDefinition = "POINT SRID 4326")
        val location: Point, // 위치 (경도, 위도)
        @Column(name = "img_url", nullable = true)
        val imgUrl: String?, // 이미지 url
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}