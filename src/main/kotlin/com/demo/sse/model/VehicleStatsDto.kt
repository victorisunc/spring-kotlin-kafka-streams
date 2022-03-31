package com.demo.sse.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class VehicleStatsDto(
    @JsonProperty("vin")
    val vin: String = UUID.randomUUID().toString(),
    @JsonProperty("lat")
    val lat: Double = 0.0,
    @JsonProperty("lon")
    val lon: Double = 0.0,
    @JsonProperty("speed")
    val speed: Double = 0.0
)