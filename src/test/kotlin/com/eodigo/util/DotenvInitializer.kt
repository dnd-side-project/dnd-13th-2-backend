package com.eodigo.util

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource

class DotenvInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load()

        val dotenvMap = dotenv.entries().associate { it.key to it.value }

        val propertySource = MapPropertySource("dotenvProperties", dotenvMap)
        applicationContext.environment.propertySources.addFirst(propertySource)
    }
}
