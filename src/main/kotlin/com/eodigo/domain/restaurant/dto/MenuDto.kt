package com.eodigo.domain.restaurant.dto

data class MenuDto(
    val id: Long, // 메뉴 id
    val name: String, // 메뉴명
    val price: Int, // 가격
)
