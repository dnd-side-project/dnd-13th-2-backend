package com.eodigo.domain.restaurant.dto

data class StoreDto(
    val storeId: Long, // 매장 id
    val storeName: String, // 매장명
    val distance: Int, // 현재 내 위치에서 거리
    val menuName: String, // 메뉴명
    val price: Int, // 가격
    val latitude: Double, // 매장 위도
    val longitude: Double, // 매장 경도
    val address: String, // 매장 주소
    val imgUrl: String?, // 이미지 url
)
