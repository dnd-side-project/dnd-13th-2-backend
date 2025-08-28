package com.eodigo.batch.processor

import com.eodigo.domain.product.entity.AnnualNationalPrice
import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.enums.MarketType
import com.eodigo.external.kamis.KamisApiClient
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
        val currentYear = LocalDate.now().year.toString()
        val kindCode = item.kindCode ?: return null

        try {
            val responseString =
                kamisApiClient.getYearlyPrices(
                    certKey = apiKey,
                    certId = certId,
                    year = currentYear,
                    itemCode = item.itemCode,
                    kindCode = kindCode,
                )

            Thread.sleep(200)

            if (responseString.isNullOrBlank() || responseString.contains("결과가 존재하지 않습니다.")) {
                return null
            }

            val priceSections = normalizePriceSections(responseString)

            if (priceSections.isNullOrEmpty()) {
                return null
            }

            // 3. 소매 가격/'상품' 등급인 첫 번째 섹션을 찾음
            val retailPriceSection =
                priceSections.find {
                    it.productClsCode == "01" && it.caption?.contains("상품") == true
                }

            if (retailPriceSection?.items == null) {
                return null
            }

            // API 응답 item을 엔티티 리스트로 변환
            return retailPriceSection.items.mapNotNull { priceItem ->
                val yearStr = priceItem.div
                val priceStr = priceItem.avgData?.replace(",", "")

                if (
                    yearStr != null &&
                        yearStr.all { it.isDigit() } &&
                        !priceStr.isNullOrBlank() &&
                        priceStr.all { it.isDigit() }
                ) {

                    AnnualNationalPrice(
                        product = item,
                        price = priceStr.toInt(),
                        surveyYear = yearStr.toInt(),
                        marketType = MarketType.RETAIL,
                    )
                } else {
                    null
                }
            }
        } catch (e: InterruptedException) {
            log.warn("API call delay was interrupted for product id: ${item.id}", e)
            Thread.currentThread().interrupt()
            return null
        } catch (e: Exception) {
            log.error(
                "Failed to process annual price for product id: ${item.id}. Error: ${e.message}"
            )
            return null
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
