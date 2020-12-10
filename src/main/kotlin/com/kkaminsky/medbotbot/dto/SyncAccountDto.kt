package com.kkaminsky.medbotbot.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class SyncAccountDto @JsonCreator constructor(
        @param:JsonProperty("username", required = true) val username :String,
        @param:JsonProperty("userMainId", required = true) val userMainId: UUID,
        @param:JsonProperty("platformId", required = true) val platformId :String,
        @param:JsonProperty("platformType", required = true) val platformType : PlatformType
)