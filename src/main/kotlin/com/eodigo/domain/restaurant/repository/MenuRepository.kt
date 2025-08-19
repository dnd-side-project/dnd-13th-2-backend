package com.eodigo.domain.restaurant.repository

import com.eodigo.domain.restaurant.entity.Menu
import org.springframework.data.jpa.repository.JpaRepository

interface MenuRepository : JpaRepository<Menu, Long> {
    fun findByNameContaining(keyword: String): List<Menu>

    fun findAllByStoreId(storeId: Long): List<Menu>
}
