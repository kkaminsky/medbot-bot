package com.kkaminsky.medbotbot.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserPlatformInfoDto @JsonCreator constructor(
        @param:JsonProperty("id", required = true) val id: String?,
        @param:JsonProperty("username", required = true) val username: String?,
        @param:JsonProperty("platform", required = true) val platform: PlatformType
)