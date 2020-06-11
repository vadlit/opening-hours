package ru.vadlit.openinghours.converters

import ru.vadlit.openinghours.api.v1.message.ScheduleMoment
import ru.vadlit.openinghours.domain.ScheduleEvent
import java.time.LocalTime

fun ScheduleMoment.toScheduleEvent() = ScheduleEvent(
    type.toEventType(),
    LocalTime.ofSecondOfDay(value.toLong())
)