package com.eodigo.domain.restaurant.repository

import com.eodigo.domain.restaurant.entity.Store
import org.springframework.data.jpa.repository.JpaRepository

interface StoreRepository : JpaRepository<Store, Long> {
    fun findByNameContaining(keyword: String): List<Store>
}
