package com.eodigo

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class EodigoApplication

fun main(args: Array<String>) {
    dotenv {
        systemProperties = true
        ignoreIfMissing = true
        ignoreIfMalformed = true
    }

    runApplication<EodigoApplication>(*args)
}
