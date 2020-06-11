package ru.vadlit.openinghours.api.v1.message

import com.fasterxml.jackson.annotation.JsonProperty

data class FormatResponse(
    @JsonProperty("formatted_days")
    val formattedDays: Map<WeekDay, String>
)