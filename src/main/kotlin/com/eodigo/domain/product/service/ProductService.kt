package com.eodigo.domain.product.service

import com.eodigo.domain.product.dto.*
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class ProductService {
    // TODO: 실제 Repository를 주입받아 DB 조회 로직으로 변경

    fun getProductHierarchy(): ProductHierarchyResponse {
        // --- Mock 데이터 생성 시작 ---
        val kindsForCabbage =
            listOf(
                KindInfo(productId = 42, kindName = "봄"),
                KindInfo(productId = 45, kindName = "여름(고랭지)"),
            )
        val cabbage = ItemInfo(itemName = "배추", productId = null, kinds = kindsForCabbage)
        val potato = ItemInfo(itemName = "감자", productId = 58, kinds = null)
        val vegetables =
            CategoryInfo(categoryName = "채소류", categoryCode = "200", items = listOf(cabbage, potato))

        val kindsForBeef =
            listOf(
                KindInfo(productId = 82, kindName = "등심"),
                KindInfo(productId = 85, kindName = "갈비"),
            )
        val beef = ItemInfo(itemName = "소", productId = null, kinds = kindsForBeef)
        val livestock = CategoryInfo(categoryName = "축산물", categoryCode = "500", items = listOf(beef))

        val foodCrops = CategoryInfo(categoryName = "식량작물", categoryCode = "100", items = emptyList())
        val specialCrops =
            CategoryInfo(categoryName = "특용작물", categoryCode = "300", items = emptyList())
        val fruits = CategoryInfo(categoryName = "과일류", categoryCode = "400", items = emptyList())
        val fisheries = CategoryInfo(categoryName = "수산물", categoryCode = "600", items = emptyList())

        val allCategories =
            listOf(foodCrops, vegetables, specialCrops, fruits, livestock, fisheries)
        // --- Mock 데이터 생성 끝 ---

        return ProductHierarchyResponse(categories = allCategories)
    }

    fun getProductTrend(productId: Long): ProductTrendResponse {
        // --- Mock 데이터 생성 시작 ---
        val annualData =
            listOf(
                AnnualPriceInfo(year = 2025, averagePrice = 4200),
                AnnualPriceInfo(year = 2024, averagePrice = 3800),
                AnnualPriceInfo(year = 2023, averagePrice = 3500),
                AnnualPriceInfo(year = 2022, averagePrice = 3200),
                AnnualPriceInfo(year = 2021, averagePrice = 3000),
                AnnualPriceInfo(year = 2020, averagePrice = 2800),
                AnnualPriceInfo(year = 2019, averagePrice = 2900),
                AnnualPriceInfo(year = 2018, averagePrice = 2700),
                AnnualPriceInfo(year = 2017, averagePrice = 3300),
                AnnualPriceInfo(year = 2016, averagePrice = 3100),
            )
        // --- Mock 데이터 생성 끝 ---

        return ProductTrendResponse(
            productId = productId,
            productName = "양배추",
            startYear = 2016,
            endYear = 2025,
            inflationRate = 35.5,
            annualData = annualData,
        )
    }

    fun getProductRanking(productId: Long): ProductRankingResponse {
        // --- Mock 데이터 생성 시작 ---
        val rankingData =
            listOf(
                RegionalPriceInfo(rank = 1, regionName = "천안", price = 2850),
                RegionalPriceInfo(rank = 2, regionName = "김해", price = 2900),
                RegionalPriceInfo(rank = 3, regionName = "창원", price = 3100),
                RegionalPriceInfo(rank = 4, regionName = "순천", price = 3150),
                RegionalPriceInfo(rank = 5, regionName = "청주", price = 3200),
                RegionalPriceInfo(rank = 6, regionName = "안동", price = 3250),
                RegionalPriceInfo(rank = 7, regionName = "강릉", price = 3300),
                RegionalPriceInfo(rank = 8, regionName = "의정부", price = 3350),
            )
        // --- Mock 데이터 생성 끝 ---

        return ProductRankingResponse(
            productId = productId,
            productName = "등심",
            surveyDate = LocalDate.now(),
            ranking = rankingData,
        )
    }
}
