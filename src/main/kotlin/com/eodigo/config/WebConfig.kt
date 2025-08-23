package com.eodigo.config

import com.eodigo.common.interceptor.ApiKeyAuthInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val apiKeyAuthInterceptor: ApiKeyAuthInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(apiKeyAuthInterceptor).addPathPatterns("/api/v1/batch/**")
    }
}
