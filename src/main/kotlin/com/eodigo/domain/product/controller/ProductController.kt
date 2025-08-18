package com.eodigo.domain.product.controller

import com.eodigo.domain.product.dto.ProductHierarchyResponse
import com.eodigo.domain.product.dto.ProductRankingResponse
import com.eodigo.domain.product.dto.ProductTrendResponse
import com.eodigo.domain.product.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val productService: ProductService) {

    @GetMapping("/hierarchy")
    fun getProductHierarchy(): ResponseEntity<ProductHierarchyResponse> {
        val hierarchyData = productService.getProductHierarchy()
        return ResponseEntity.ok(hierarchyData)
    }

    @GetMapping("/{productId}/trends")
    fun getProductTrend(@PathVariable productId: Long): ResponseEntity<ProductTrendResponse> {
        val trendData = productService.getProductTrend(productId)
        return ResponseEntity.ok(trendData)
    }

    @GetMapping("/{productId}/rankings")
    fun getProductRanking(@PathVariable productId: Long): ResponseEntity<ProductRankingResponse> {
        val rankingData = productService.getProductRanking(productId)
        return ResponseEntity.ok(rankingData)
    }
}
