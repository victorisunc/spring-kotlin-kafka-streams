package com.demo.sse.engine

import com.demo.sse.config.REQUEST_INTERVAL_SEC
import com.demo.sse.config.VEHICLE_POSITIONS_TOPIC
import com.demo.sse.config.VEHICLE_STATS_STORE
import com.demo.sse.config.serdeConfig
import com.demo.sse.engine.CalcUtils.Geo.distanceInKm
import com.demo.sse.engine.CalcUtils.Geo.speed
import com.demo.sse.model.VehicleStats
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class StreamConsumer {

    private val logger = KotlinLogging.logger {}

    private val valueSpecificAvroSerde: Serde<VehicleStats> = SpecificAvroSerde<VehicleStats>()

    @PostConstruct
    fun init() {
        valueSpecificAvroSerde.configure(serdeConfig, false) // `false` for record values
    }

    @Bean("kafkaStreamProcessing")
    fun vehicleStatsKStream(streamsBuilder: StreamsBuilder): KStream<String, VehicleStats> {
        logger.info("Start processing........ ")

        val stream: KStream<String, VehicleStats> = streamsBuilder.stream(VEHICLE_POSITIONS_TOPIC)

        val aggregatedStream = stream.groupByKey()
            .aggregate(
                { VehicleStats() },
                { vin, position, aggregateStat ->
                    val stats = VehicleStats(
                        vin,
                        position.lat,
                        position.lon,
                        speed(
                            distanceInKm(position.lat, position.lon, aggregateStat.lat, aggregateStat.lon),
                            REQUEST_INTERVAL_SEC
                        )
                    )
                    logger.info("Vehicle Stats - vin: ${stats.vin}, lat: ${stats.lat}, lon: ${stats.lon}, speed: ${stats.speed} ")
                    stats
                }, Materialized.`as`<String, VehicleStats, KeyValueStore<Bytes, ByteArray>>(VEHICLE_STATS_STORE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(valueSpecificAvroSerde)
            ).toStream()
        // Here we can also send the result to another topic, or save it in any other DB
        return aggregatedStream
    }

}
