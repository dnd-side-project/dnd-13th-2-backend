package com.eodigo.external.kamis

import com.eodigo.common.initializer.KamisProductInfoResponse
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

interface KamisApiClient {

    @GetExchange("/service/price/xml.do?action=productInfo&p_returntype=json")
    fun getProductInfo(
        @RequestParam("p_cert_key") certKey: String,
        @RequestParam("p_cert_id") certId: String,
    ): KamisProductInfoResponse?
}
