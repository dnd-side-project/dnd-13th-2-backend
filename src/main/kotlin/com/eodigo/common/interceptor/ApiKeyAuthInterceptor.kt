package com.eodigo.common.interceptor

import com.eodigo.common.exception.InvalidApiKeyException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ApiKeyAuthInterceptor(@Value("\${admin.api-key}") private val serverApiKey: String) :
    HandlerInterceptor {

    companion object {
        private const val API_KEY_HEADER_NAME = "X-API-KEY"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val clientApiKey = request.getHeader(API_KEY_HEADER_NAME)

        if (clientApiKey == null || clientApiKey != serverApiKey) {
            throw InvalidApiKeyException()
        }
        return true
    }
}
