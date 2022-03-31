package com.demo.sse.engine

import com.demo.sse.config.VEHICLE_POSITIONS_TOPIC
import com.demo.sse.model.VehicleStats
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Repository

@Repository
class VehicleStatsProducer(val producer: KafkaTemplate<String, VehicleStats>) {
    private val logger = KotlinLogging.logger {}

    fun send(message: VehicleStats) {
        producer.send(VEHICLE_POSITIONS_TOPIC, message.vin.toString(), message)
            .addCallback(
                { result -> logger.debug("Success: $result") },
                { ex -> logger.error("Failed to send message, ex: $ex") }
            )
    }
}