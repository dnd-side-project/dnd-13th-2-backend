package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.DailyRegionalPrice
import org.springframework.data.jpa.repository.JpaRepository

interface DailyRegionalPriceRepository : JpaRepository<DailyRegionalPrice, Long>
