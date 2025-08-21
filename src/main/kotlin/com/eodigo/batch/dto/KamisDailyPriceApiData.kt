package com.eodigo.batch.dto

import com.eodigo.external.kamis.KamisDailyPriceItemDto

data class KamisDailyPriceApiData(val regionCode: String, val item: KamisDailyPriceItemDto)
