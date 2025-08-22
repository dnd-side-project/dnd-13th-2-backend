package com.eodigo.batch.processor

import com.eodigo.batch.dto.KamisDailyPriceApiData
import com.eodigo.domain.product.entity.DailyRegionalPrice
import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.entity.Region
import com.eodigo.domain.product.enums.MarketType
import com.eodigo.domain.product.enums.ProductSource
import com.eodigo.domain.product.repository.ProductRepository
import com.eodigo.domain.product.repository.RegionRepository
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

class KamisDailyPriceProcessor(
    private val productRepository: ProductRepository,
    private val regionRepository: RegionRepository,
    private val surveyDate: LocalDate,
) : ItemProcessor<KamisDailyPriceApiData, DailyRegionalPrice> {

    private val log = LoggerFactory.getLogger(javaClass)

    // 1. Product와 Region 정보를 메모리에 캐싱
    private val productCache: Map<String, Product> by lazy {
        productRepository.findAllBySource(ProductSource.KAMIS).associateBy {
            createProductKey(it.itemCode, it.kindCode)
        }
    }
    private val regionCache: Map<String, Region> by lazy {
        regionRepository.findAll().associateBy { it.code }
    }

    override fun process(wrapper: KamisDailyPriceApiData): DailyRegionalPrice? {
        // 2. 가격 정보가 유효하지 않으면 필터링
        val item = wrapper.item
        val priceStr = item.price?.replace(",", "")
        if (priceStr.isNullOrBlank() || priceStr == "-") {
            return null
        }

        // 3. 캐시에서 Product와 Region 엔티티를 찾음
        val productKey = createProductKey(item.itemCode, item.kindCode)
        val product = productCache[productKey]
        val region = regionCache[wrapper.regionCode]

        // 4. 매핑되는 Product나 Region이 없으면 무시
        if (product == null || region == null) {
            log.error("Failed to match product or region: product: $product, region: $region")
            return null
        }

        // 5. 모든 정보가 유효하면, DailyRegionalPrice 엔티티를 생성하여 반환
        return DailyRegionalPrice(
            product = product,
            region = region,
            price = priceStr.toInt(),
            surveyDate = surveyDate,
            marketType = MarketType.RETAIL,
        )
    }

    private fun createProductKey(itemCode: String?, kindCode: String?): String {
        return "$itemCode-$kindCode"
    }
}
