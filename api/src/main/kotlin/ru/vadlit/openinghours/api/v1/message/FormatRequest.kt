package ru.vadlit.openinghours.api.v1.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

data class FormatRequest @JsonCreator constructor(
    @field:Valid
    @JsonProperty("monday")
    val monday: List<ScheduleMoment>?,

    @field:Valid
    @JsonProperty("tuesday")
    val tuesday: List<ScheduleMoment>?,

    @field:Valid
    @JsonProperty("wednesday")
    val wednesday: List<ScheduleMoment>?,

    @field:Valid
    @JsonProperty("thursday")
    val thursday: List<ScheduleMoment>?,

    @field:Valid
    @JsonProperty("friday")
    val friday: List<ScheduleMoment>?,

    @field:Valid
    @JsonProperty("saturday")
    val saturday: List<ScheduleMoment>?,

    @field:Valid
    @JsonProperty("sunday")
    val sunday: List<ScheduleMoment>?
)