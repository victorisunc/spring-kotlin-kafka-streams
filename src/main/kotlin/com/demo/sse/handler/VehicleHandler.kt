package com.demo.sse.handler

import com.demo.sse.model.VehiclePositionDto
import com.demo.sse.repository.VehicleRepository
import kotlinx.coroutines.reactive.asPublisher
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.sse
import reactor.core.publisher.Mono

@Component
class VehicleHandler @Autowired constructor(
    private var vehicleRepository: VehicleRepository
) {

    private val logger = KotlinLogging.logger {}

    fun postLocation(request: ServerRequest) =
        ServerResponse
            .ok()
            .body(
                vehicleRepository.postPosition(
                    request.bodyToMono(
                        VehiclePositionDto::class.java
                    ).map { it.copy(vin = request.pathVariable("vin")) }
                )
            )

    fun stream(serverRequest: ServerRequest): Mono<ServerResponse> {

        logger.info("Request to stream vehicle stats for vin: ${serverRequest.pathVariable("vin")}")

        return ServerResponse
            .ok()
            .sse()
            .body(
                vehicleRepository
                    .streamVehicleStats(serverRequest.pathVariable("vin"))
                    .asPublisher()
            )
    }
}