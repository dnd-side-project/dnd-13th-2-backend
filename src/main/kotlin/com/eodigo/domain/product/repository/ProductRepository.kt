package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.enums.ProductSource
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    fun existsBySource(source: ProductSource): Boolean
}
