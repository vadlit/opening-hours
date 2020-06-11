package ru.vadlit.openinghours.api.v1.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class ScheduleMoment @JsonCreator constructor(
    @JsonProperty("type")
    val type: MomentType,

    @field:Min(0)
    @field:Max(86399)
    @JsonProperty("value", required = true)
    val value: Int
)