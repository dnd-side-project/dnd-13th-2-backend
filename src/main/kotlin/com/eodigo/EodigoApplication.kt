package com.eodigo

import io.github.cdimascio.dotenv.dotenv
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableBatchProcessing
@EnableJpaAuditing
@SpringBootApplication
class EodigoApplication

fun main(args: Array<String>) {
    dotenv {
        systemProperties = true
        ignoreIfMissing = true
        ignoreIfMalformed = true
    }

    runApplication<EodigoApplication>(*args)
}
