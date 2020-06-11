package ru.vadlit.openinghours.api.v1.message

import com.fasterxml.jackson.annotation.JsonProperty

enum class WeekDay {
    @JsonProperty("monday")
    MONDAY,
    @JsonProperty("tuesday")
    TUESDAY,
    @JsonProperty("wednesday")
    WEDNESDAY,
    @JsonProperty("thursday")
    THURSDAY,
    @JsonProperty("friday")
    FRIDAY,
    @JsonProperty("saturday")
    SATURDAY,
    @JsonProperty("sunday")
    SUNDAY
}