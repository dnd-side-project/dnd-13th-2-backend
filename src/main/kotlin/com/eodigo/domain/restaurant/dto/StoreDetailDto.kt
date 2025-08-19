package com.eodigo.domain.restaurant.dto

import com.eodigo.domain.restaurant.enums.StoreCategory

data class StoreDetailDto(
    val storeId: Long, // 매장 id
    val name: String, // 매장명,
    val distance: Int, // 현재 내 위치에서 거리
    val category: StoreCategory, // 카테고리 (CAFE, RESTAURANT)
    val address: String, // 매장 주소
    val latitude: Double, // 매장 위도
    val longitude: Double, // 매장 경도
    val imgUrl: String?, // 이미지 url
    val menus: List<MenuDto>,
)
