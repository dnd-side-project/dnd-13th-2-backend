package com.eodigo.config

import com.eodigo.external.kamis.KamisApiClient
import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.ChannelOption
import java.time.Duration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        val httpClient =
            HttpClient.create()
                .followRedirect(true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(10))

        val exchangeStrategies =
            ExchangeStrategies.builder()
                .codecs { configurer ->
                    configurer
                        .defaultCodecs()
                        .jackson2JsonDecoder(
                            Jackson2JsonDecoder(ObjectMapper(), MediaType.TEXT_PLAIN)
                        )
                }
                .build()

        return WebClient.builder()
            .baseUrl("http://www.kamis.or.kr")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(exchangeStrategies)
            .build()
    }

    @Bean
    fun kamisApiClient(webClient: WebClient): KamisApiClient {
        val factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build()

        return factory.createClient(KamisApiClient::class.java)
    }
}
