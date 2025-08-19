package com.eodigo.domain.product.service

import com.eodigo.domain.product.dto.*
import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.exception.ProductNotFoundException
import com.eodigo.domain.product.exception.ProductRankingNotFoundException
import com.eodigo.domain.product.exception.ProductTrendNotFoundException
import com.eodigo.domain.product.repository.AnnualNationalPriceRepository
import com.eodigo.domain.product.repository.DailyRegionalPriceRepository
import com.eodigo.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val dailyRegionalPriceRepository: DailyRegionalPriceRepository,
    private val annualNationalPriceRepository: AnnualNationalPriceRepository,
) {

    companion object {
        private const val UNKNOWN_KIND_NAME = "이름 없음"
    }

    fun getProductHierarchy(): List<CategoryInfo> {
        val allProducts = productRepository.findAllByOrderByIdAsc()

        val categories =
            allProducts
                .groupBy { it.categoryCode }
                .map { (categoryCode, productsInCategory) ->
                    val firstProduct = productsInCategory.first()
                    CategoryInfo(
                        categoryName = firstProduct.categoryName,
                        categoryCode = categoryCode,
                        items =
                            productsInCategory
                                .groupBy { it.itemCode }
                                .map { (_, productsInItem) ->
                                    val firstItem = productsInItem.first()
                                    // 품종(kind)이 있는지 여부로 2단계/3단계 계층 구분
                                    if (productsInItem.size == 1 && firstItem.kindName == null) {
                                        // 2단계 계층 (품종 없음)
                                        ItemInfo(
                                            itemName = firstItem.itemName,
                                            productId = firstItem.id,
                                            kinds = null,
                                        )
                                    } else {
                                        // 3단계 계층 (품종 있음)
                                        ItemInfo(
                                            itemName = firstItem.itemName,
                                            productId = null,
                                            kinds =
                                                productsInItem.map { product ->
                                                    KindInfo(
                                                        productId =
                                                            requireNotNull(product.id) {
                                                                "Persisted Product.id must not be null"
                                                            },
                                                        kindName =
                                                            product.kindName ?: UNKNOWN_KIND_NAME,
                                                    )
                                                },
                                        )
                                    }
                                },
                    )
                }

        return categories
    }

    fun getProductRanking(productId: Long): ProductRankingResponse {
        val product = findProductById(productId)
        val latestPrices = dailyRegionalPriceRepository.findLatestPricesByProductId(productId)

        if (latestPrices.isEmpty()) {
            throw ProductRankingNotFoundException()
        }

        val rankedPrices =
            latestPrices
                .sortedBy { it.price }
                .mapIndexed { index, priceInfo ->
                    RegionalPriceInfo(
                        rank = index + 1,
                        regionName = priceInfo.region.regionName,
                        price = priceInfo.price,
                    )
                }

        return ProductRankingResponse(
            productId = productId,
            productName = product.name,
            surveyDate = latestPrices.first().surveyDate,
            ranking = rankedPrices,
        )
    }

    fun getProductTrend(productId: Long): ProductTrendResponse {
        val product = findProductById(productId)
        val allAnnualPrices =
            annualNationalPriceRepository.findByProductIdOrderBySurveyYearDesc(productId)

        if (allAnnualPrices.isEmpty()) {
            throw ProductTrendNotFoundException()
        }

        val latestYear = allAnnualPrices.first().surveyYear
        val startYearOfLast10Years = latestYear - 9
        val last10YearsPrices = allAnnualPrices.filter { it.surveyYear >= startYearOfLast10Years }

        if (last10YearsPrices.isEmpty()) {
            throw ProductTrendNotFoundException()
        }

        val firstPrice = last10YearsPrices.last().price
        val lastPrice = last10YearsPrices.first().price

        val inflationRate =
            if (firstPrice > 0) {
                ((lastPrice - firstPrice).toDouble() / firstPrice) * 100
            } else {
                0.0
            }

        return ProductTrendResponse(
            productId = productId,
            productName = product.name,
            startYear = last10YearsPrices.last().surveyYear,
            endYear = last10YearsPrices.first().surveyYear,
            inflationRate = inflationRate,
            annualData = last10YearsPrices.map { AnnualPriceInfo(it.surveyYear, it.price) },
        )
    }

    private fun findProductById(productId: Long): Product {
        return productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
    }
}
