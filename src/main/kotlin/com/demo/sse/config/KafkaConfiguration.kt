package com.demo.sse.config

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin.NewTopics
import java.util.*

@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaConfiguration {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun appTopics(): NewTopics {
        return NewTopics(
            TopicBuilder.name(VEHICLE_POSITIONS_TOPIC).build()
        )
    }

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun defaultKafkaStreamsConfig(): KafkaStreamsConfiguration {
        val props: MutableMap<String, Any> = HashMap()
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = KAFKA_HOSTS
        props[StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG] =
            LogAndContinueExceptionHandler::class.java
        props[AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = SCHEMA_REGISTRY_URL
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "vehicle-position-stream-2"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = SpecificAvroSerde::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = "vehicle-positions-stream-group"
        return KafkaStreamsConfiguration(props)
    }

    @Bean
    fun configurer(): StreamsBuilderFactoryBeanConfigurer? {
        return StreamsBuilderFactoryBeanConfigurer { fb: StreamsBuilderFactoryBean ->
            fb.setStateListener { newState: KafkaStreams.State, oldState: KafkaStreams.State ->
                logger.debug("State transition from $oldState to $newState")
            }
        }
    }
}

// constants for topics and global configuration
const val VEHICLE_POSITIONS_TOPIC = "vehicle-positions-topic"
const val VEHICLE_STATS_STORE = "vehicle-stats-store"
const val REQUEST_INTERVAL_SEC = 1L
const val SCHEMA_REGISTRY_URL = "http://schema-registry:8081"
val KAFKA_HOSTS: List<String> = listOf("kafka:29092")
val serdeConfig: MutableMap<String, String> = Collections.singletonMap(
    AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL
)
