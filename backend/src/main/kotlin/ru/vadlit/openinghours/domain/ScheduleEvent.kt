package ru.vadlit.openinghours.domain

import java.time.LocalTime

data class ScheduleEvent (
    val type: EventType,
    val time: LocalTime
)