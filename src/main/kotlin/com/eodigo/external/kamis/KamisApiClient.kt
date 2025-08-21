package com.eodigo.external.kamis

import com.eodigo.common.initializer.KamisProductInfoResponse
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

interface KamisApiClient {

    @GetExchange("/service/price/xml.do?action=productInfo&p_returntype=json")
    fun getProductInfo(
        @RequestParam("p_cert_key") certKey: String,
        @RequestParam("p_cert_id") certId: String,
    ): KamisProductInfoResponse?

    @GetExchange(
        "/service/price/xml.do?action=dailyPriceByCategoryList&p_product_cls_code=01&p_returntype=json"
    )
    fun getDailyPrices(
        @RequestParam("p_cert_key") certKey: String,
        @RequestParam("p_cert_id") certId: String,
        @RequestParam("p_regday") regDay: String,
        @RequestParam("p_item_category_code") categoryCode: String,
        @RequestParam("p_country_code") countryCode: String,
    ): KamisDailyPriceResponse?
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class KamisDailyPriceResponse(
    val condition: List<Map<String, Any>>? = emptyList(), // condition 필드 추가 (내용물은 쓰지 않으므로 Map으로 받음)
    val data: KamisDailyPriceData? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KamisDailyPriceData(val errorCode: String?, val items: List<KamisDailyPriceItemDto>?) {
    companion object {
        @JvmStatic
        @JsonCreator
        fun from(value: Any): KamisDailyPriceData {
            return when (value) {
                // 1. value가 객체(Map)인 경우 (성공 케이스)
                is Map<*, *> -> {
                    val errorCode = value["error_code"] as? String
                    val itemsNode = value["item"] as? List<*> ?: emptyList<Any>()

                    val items =
                        itemsNode.mapNotNull { itemMap ->
                            if (itemMap is Map<*, *>) {
                                KamisDailyPriceItemDto(
                                    itemName = itemMap["item_name"] as? String,
                                    itemCode = itemMap["item_code"] as? String,
                                    kindName = itemMap["kind_name"] as? String,
                                    kindCode = itemMap["kind_code"] as? String,
                                    rank = itemMap["rank"] as? String,
                                    rankCode = itemMap["rank_code"] as? String,
                                    unit = itemMap["unit"] as? String,
                                    price = itemMap["dpr1"] as? String,
                                )
                            } else {
                                null
                            }
                        }
                    KamisDailyPriceData(errorCode, items)
                }
                // 2. value가 배열(List)인 경우 (데이터 없음 또는 에러 케이스)
                is List<*> -> {
                    // 배열의 첫 번째 요소(에러 코드 문자열)를 errorCode로 사용
                    val errorCode = value.firstOrNull() as? String
                    // 이 경우 item 리스트는 비어있음
                    KamisDailyPriceData(errorCode, emptyList())
                }
                // 3. 그 외 예상치 못한 타입인 경우
                else -> KamisDailyPriceData("999", emptyList())
            }
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class KamisDailyPriceItemDto(
    // 모든 필드를 nullable로 선언하여 안정성 확보
    val itemName: String?,
    val itemCode: String?,
    val kindName: String?,
    val kindCode: String?,
    val rank: String?,
    val rankCode: String?,
    val unit: String?,
    val price: String?,
)
