package ru.vadlit.openinghours.converters

import ru.vadlit.openinghours.api.v1.message.MomentType
import ru.vadlit.openinghours.domain.EventType

fun MomentType.toEventType() = when(this) {
    MomentType.OPEN -> EventType.OPEN
    MomentType.CLOSE -> EventType.CLOSE
}