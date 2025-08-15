package com.eodigo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class EodigoApplication

fun main(args: Array<String>) {
    runApplication<EodigoApplication>(*args)
}
