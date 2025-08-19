package com.eodigo.domain.restaurant.dto

import com.eodigo.domain.restaurant.enums.SortType
import com.eodigo.domain.restaurant.enums.StoreCategory

data class SearchRequest(
    val menuName: String, // 메뉴명
    val category: StoreCategory, // 카테고리 (CAFE, RESTAURANT)
    val userLat: Double, // 현재 위치 위도
    val userLng: Double, // 현재 위치 경도
    val sort: SortType, // 정렬 (PRICE, DISTANCE)
    val southWestLat: Double, // 왼쪽 하단 좌표의 위도
    val southWestLng: Double, // 왼쪽 하단 좌표의 경도
    val northEastLat: Double, // 우측 상단 좌표의 위도
    val northEastLng: Double, // 우측 상단 좌표의 경도
)
