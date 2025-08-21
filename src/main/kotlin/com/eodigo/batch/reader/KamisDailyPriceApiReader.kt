package com.eodigo.batch.reader

import com.eodigo.batch.dto.KamisDailyPriceApiData
import com.eodigo.external.kamis.KamisApiClient
import com.eodigo.external.kamis.KamisDailyPriceItemDto
import org.springframework.batch.item.ItemReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class KamisDailyPriceApiReader(
    private val kamisApiClient: KamisApiClient,
    private val apiKey: String,
    private val certId: String,
    private val categoryCodes: List<String>,
    private val regionCodes: List<String>
) : ItemReader<KamisDailyPriceApiData> {

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

        // 5. 현재 인덱스의 아이템을 반환하고, 인덱스를 1 증가시킴
        return allItems!![currentItemIndex++]
    }

    private fun initialize() {
        allItems = mutableListOf()
        currentItemIndex = 0
        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        // 당일 새벽에는 가격이 업데이트되지 않음

        for (categoryCode in categoryCodes) {
            for (regionCode in regionCodes) {
                val response = kamisApiClient.getDailyPrices(
                    certKey = apiKey,
                    certId = certId,
                    regDay = yesterday,
                    categoryCode = categoryCode,
                    countryCode = regionCode
                )

                if (response?.data?.errorCode == "000") {
                    response.data.items?.let { items ->
                        val wrappedItems = items.map { item ->
                            KamisDailyPriceApiData(regionCode = regionCode, item = item)
                        }
                        allItems?.addAll(wrappedItems)
                    }
                }
            }
        }
        println("KamisDailyPriceApiReader: Total ${allItems?.size} items initialized.")
    }
}