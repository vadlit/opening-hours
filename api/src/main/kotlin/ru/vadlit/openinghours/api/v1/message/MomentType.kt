package ru.vadlit.openinghours.api.v1.message

import com.fasterxml.jackson.annotation.JsonProperty

enum class MomentType {
    @JsonProperty("open")
    OPEN,
    @JsonProperty("close")
    CLOSE
}