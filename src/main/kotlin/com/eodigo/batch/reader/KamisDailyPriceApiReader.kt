package com.eodigo.batch.reader

import com.eodigo.batch.dto.KamisDailyPriceApiData
import com.eodigo.external.kamis.KamisApiClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader

class KamisDailyPriceApiReader(
    private val kamisApiClient: KamisApiClient,
    private val apiKey: String,
    private val certId: String,
    private val categoryCodes: List<String>,
    private val regionCodes: List<String>,
    private val surveyDate: LocalDate,
) : ItemReader<KamisDailyPriceApiData> {

    companion object {
        private const val API_CALL_DELAY_MS = 200L
        private const val ERROR_CODE_SUCCESS = "000"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    // 1. 처리할 모든 API 호출 결과(가격 아이템)를 담아둘 리스트
    private var allItems: MutableList<KamisDailyPriceApiData>? = null
    // 2. 현재 읽고 있는 아이템의 인덱스
    private var currentItemIndex = 0

    override fun read(): KamisDailyPriceApiData? {
        // 3. 첫 read() 호출 시에만 API 통신을 통해 모든 데이터를 미리 가져옴 (초기화)
        if (allItems == null) {
            initialize()
        }

        // 4. 모든 아이템을 다 읽었으면 null을 반환하여 배치의 끝을 알림
        if (currentItemIndex >= (allItems?.size ?: 0)) {
            return null
        }

        val list = allItems ?: return null

        // 5. 현재 인덱스의 아이템을 반환하고, 인덱스를 1 증가시킴
        return list[currentItemIndex++]
    }

    private fun initialize() {
        allItems = mutableListOf()
        currentItemIndex = 0

        val regDay = surveyDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        for (categoryCode in categoryCodes) {
            for (regionCode in regionCodes) {
                try {
                    val response =
                        kamisApiClient.getDailyPrices(
                            certKey = apiKey,
                            certId = certId,
                            regDay = regDay,
                            categoryCode = categoryCode,
                            countryCode = regionCode,
                        )

                    if (response?.data?.errorCode == ERROR_CODE_SUCCESS) {
                        response.data.items?.let { items ->
                            val wrappedItems =
                                items.map { item ->
                                    KamisDailyPriceApiData(regionCode = regionCode, item = item)
                                }
                            allItems?.addAll(wrappedItems)
                        }
                    }
                    Thread.sleep(API_CALL_DELAY_MS)
                } catch (e: InterruptedException) {
                    log.warn("API call delay was interrupted", e)
                    Thread.currentThread().interrupt()
                } catch (e: Exception) {
                    log.error(
                        "Failed to fetch data for category: {}, region: {}. Error: {}",
                        categoryCode,
                        regionCode,
                        e.message,
                    )
                }
            }
        }
        log.info("KamisDailyPriceApiReader: Total ${allItems?.size} items initialized.")
    }
}
