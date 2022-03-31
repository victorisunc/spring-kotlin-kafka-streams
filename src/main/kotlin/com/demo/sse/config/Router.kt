package com.demo.sse.config

import com.demo.sse.handler.VehicleHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class Router {

    @Bean
    fun routes(vehicleHandler: VehicleHandler) =
            router {
                accept(MediaType.APPLICATION_JSON)
                        .nest {
                            POST("/vehicle/{vin}",
                                    vehicleHandler::postLocation)
                        }
                accept(MediaType.TEXT_EVENT_STREAM)
                        .nest {
                            GET("/vehicle/{vin}/stream",
                                    vehicleHandler::stream)
                        }
            }
}