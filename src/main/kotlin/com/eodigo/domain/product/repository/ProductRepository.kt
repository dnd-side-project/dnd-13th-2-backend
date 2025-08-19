package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllByOrderByIdAsc(): List<Product>
}
