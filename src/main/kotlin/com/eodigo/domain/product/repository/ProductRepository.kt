package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.enums.ProductSource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductRepository : JpaRepository<Product, Long> {
    fun existsBySource(source: ProductSource): Boolean

    fun findAllBySource(source: ProductSource): List<Product>

    fun findAllByOrderByIdAsc(): List<Product>

    fun findByNameContaining(keyword: String): List<Product>

    @Query("SELECT DISTINCT p.categoryCode FROM Product p")
    fun findDistinctCategoryCodes(): List<String>
}
