package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.AnnualNationalPrice
import org.springframework.data.jpa.repository.JpaRepository

interface AnnualNationalPriceRepository : JpaRepository<AnnualNationalPrice, Long>
