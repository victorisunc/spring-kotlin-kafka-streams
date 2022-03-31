package com.demo.sse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class FleetStateApplication

fun main(args: Array<String>) {
    runApplication<FleetStateApplication>(*args)
}
