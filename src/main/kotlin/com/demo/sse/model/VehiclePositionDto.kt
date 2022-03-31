package com.demo.sse.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class VehiclePositionDto(
    @JsonProperty("vin")
    val vin: String = UUID.randomUUID().toString(),
    @JsonProperty("lat")
    val lat: Double,
    @JsonProperty("lon")
    val lon: Double
)

