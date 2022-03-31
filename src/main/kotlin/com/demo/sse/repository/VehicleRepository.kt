package com.demo.sse.repository

import com.demo.sse.config.REQUEST_INTERVAL_SEC
import com.demo.sse.config.VEHICLE_STATS_STORE
import com.demo.sse.engine.VehicleStatsProducer
import com.demo.sse.model.VehiclePositionDto
import com.demo.sse.model.VehicleStats
import com.demo.sse.model.VehicleStatsDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Repository
class VehicleRepository(
    private val producer: VehicleStatsProducer,
    private val factoryBean: StreamsBuilderFactoryBean
) {
    fun postPosition(monoVehicle: Mono<VehiclePositionDto>): Mono<VehiclePositionDto> {
        return monoVehicle.flatMap {
            producer.send(VehicleStats(it.vin, it.lat, it.lon, 0.0))
            Mono.just(it)
        }
    }

    fun streamVehicleStats(vin: String): Flow<VehicleStatsDto> {
        val kafkaStreams: KafkaStreams = factoryBean.kafkaStreams
        val statsFromStore: ReadOnlyKeyValueStore<String, VehicleStats?> =
            kafkaStreams.store(StoreQueryParameters.fromNameAndType(VEHICLE_STATS_STORE, QueryableStoreTypes.keyValueStore()))

        return Flux.interval(Duration.ofSeconds(REQUEST_INTERVAL_SEC)).flatMap {
            val stats = statsFromStore.get(vin)
            stats?.let {
                Mono.just(VehicleStatsDto(vin, it.lat, it.lon, it.speed))
            } ?: Mono.just(VehicleStatsDto(vin, 0.0, 0.0, 0.0))
        }.asFlow()
    }
}