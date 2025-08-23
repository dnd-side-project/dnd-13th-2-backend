package com.eodigo.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    companion object {
        const val API_KEY_SCHEME_NAME = "ApiKeyAuth"
    }

    private val apiKey: SecurityScheme =
        SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)
            .name("X-API-KEY")
            .description("배치 API 인증을 위한 API Key")

    @Bean
    fun openAPI(): OpenAPI {
        val info: Info =
            Info()
                .title("어디고 API 명세서")
                .description("오프라인 가격 비교 서비스 '어디고'의 API 명세서입니다.")
                .version("v1.0")

        val securityRequirement = SecurityRequirement().addList(API_KEY_SCHEME_NAME)

        return OpenAPI()
            .components(Components().addSecuritySchemes(API_KEY_SCHEME_NAME, apiKey))
            .addSecurityItem(securityRequirement)
            .info(info)
    }
}
