package com.kkaminsky.medbotbot.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class HealthReportDto @JsonCreator constructor(
        @param:JsonProperty("userPlatformId", required = true) val userPlatformId: String,
        @param:JsonProperty("drugName", required = true) val drugName: String,
        @param:JsonProperty("receiptId", required = true) val receiptId: UUID
)