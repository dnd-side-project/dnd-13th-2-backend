package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.Region
import org.springframework.data.jpa.repository.JpaRepository

interface RegionRepository : JpaRepository<Region, Long>
