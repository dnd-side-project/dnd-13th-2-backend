package com.eodigo.batch.processor

import com.eodigo.domain.product.entity.AnnualNationalPrice
import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.enums.MarketType
import com.eodigo.external.kamis.KamisApiClient
import com.eodigo.external.kamis.KamisYearlyPriceItemDto
import com.eodigo.external.kamis.KamisYearlyPriceSectionDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

class KamisAnnualPriceProcessor(
    private val kamisApiClient: KamisApiClient,
    private val apiKey: String,
    private val certId: String,
) : ItemProcessor<Product, List<AnnualNationalPrice>> {

    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun process(item: Product): List<AnnualNationalPrice>? {
        val kindCode = item.kindCode ?: return null

        try {
            // 1. API 호출
            val responseString = fetchAnnualPriceData(item.itemCode, kindCode)
            if (responseString == null) {
                return null
            }

            // 2. 응답 파싱 및 필터링
            val priceItems = parseAndFilterPriceItems(responseString)
            if (priceItems == null) {
                return null
            }

            // 3. 엔티티로 변환
            return convertToEntities(priceItems, item)
        } catch (e: InterruptedException) {
            log.warn("Annual price processing was interrupted for product id: ${item.id}", e)
            Thread.currentThread().interrupt()
            return null
        } catch (e: Exception) {
            log.error(
                "Failed to process annual price for product id: ${item.id}. Error: ${e.message}"
            )
            return null
        }
    }

    /** API를 호출하고 응답 문자열을 반환합니다. 유효하지 않은 응답은 null을 반환합니다. */
    private fun fetchAnnualPriceData(itemCode: String, kindCode: String): String? {
        val currentYear = LocalDate.now().year.toString()
        val responseString =
            kamisApiClient.getYearlyPrices(
                certKey = apiKey,
                certId = certId,
                year = currentYear,
                itemCode = itemCode,
                kindCode = kindCode,
            )

        Thread.sleep(200)

        if (responseString.isNullOrBlank() || responseString.contains("결과가 존재하지 않습니다.")) {
            return null
        }
        return responseString
    }

    /** API 응답을 파싱하여 유효한 가격 아이템 리스트를 반환합니다. 유효 데이터가 없으면 null을 반환합니다. */
    private fun parseAndFilterPriceItems(responseString: String): List<KamisYearlyPriceItemDto>? {
        val priceSections = normalizePriceSections(responseString)
        if (priceSections.isNullOrEmpty()) {
            return null
        }

        val retailPriceSection =
            priceSections.find { it.productClsCode == "01" && it.caption?.contains("상품") == true }

        return retailPriceSection?.items
    }

    /** DTO 리스트를 엔티티 리스트로 변환합니다. */
    private fun convertToEntities(
        priceItems: List<KamisYearlyPriceItemDto>,
        product: Product,
    ): List<AnnualNationalPrice> {
        return priceItems.mapNotNull { priceItem ->
            val yearStr = priceItem.div
            val priceStr = priceItem.avgData?.replace(",", "")

            if (
                yearStr?.all(Char::isDigit) == true &&
                    priceStr?.all(Char::isDigit) == true &&
                    priceStr.isNotEmpty()
            ) {
                AnnualNationalPrice(
                    product = product,
                    price = priceStr.toInt(),
                    surveyYear = yearStr.toInt(),
                    marketType = MarketType.RETAIL,
                )
            } else {
                null
            }
        }
    }

    private fun normalizePriceSections(responseString: String): List<KamisYearlyPriceSectionDto>? {
        try {
            // 전체 응답을 Map으로 파싱
            val responseMap: Map<String, Any> = objectMapper.readValue(responseString)
            val priceData = responseMap["price"]

            return when (priceData) {
                // price가 리스트인 경우
                is List<*> ->
                    objectMapper.convertValue(
                        priceData,
                        objectMapper.typeFactory.constructCollectionType(
                            List::class.java,
                            KamisYearlyPriceSectionDto::class.java,
                        ),
                    )
                // price가 단일 객체(Map)인 경우
                is Map<*, *> -> {
                    val singleSection =
                        objectMapper.convertValue(priceData, KamisYearlyPriceSectionDto::class.java)
                    listOf(singleSection)
                }
                else -> null
            }
        } catch (e: Exception) {
            log.error("Failed to parse yearly price response: {}", responseString, e)
            return null
        }
    }
}
