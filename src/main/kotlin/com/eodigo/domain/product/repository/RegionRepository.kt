package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.Region
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RegionRepository : JpaRepository<Region, Long> {
    @Query("SELECT r.code FROM Region r") fun findAllCodes(): List<String>
}
