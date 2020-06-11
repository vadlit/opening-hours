package ru.vadlit.openinghours.converters

import ru.vadlit.openinghours.api.v1.message.WeekDay
import java.time.DayOfWeek

fun WeekDay.toDayOfWeek() = when(this) {
    WeekDay.MONDAY -> DayOfWeek.MONDAY
    WeekDay.TUESDAY -> DayOfWeek.TUESDAY
    WeekDay.WEDNESDAY -> DayOfWeek.WEDNESDAY
    WeekDay.THURSDAY -> DayOfWeek.THURSDAY
    WeekDay.FRIDAY -> DayOfWeek.FRIDAY
    WeekDay.SATURDAY -> DayOfWeek.SATURDAY
    WeekDay.SUNDAY -> DayOfWeek.SUNDAY
}

fun DayOfWeek.toWeekDay() = when(this) {
    DayOfWeek.MONDAY -> WeekDay.MONDAY
    DayOfWeek.TUESDAY -> WeekDay.TUESDAY
    DayOfWeek.WEDNESDAY -> WeekDay.WEDNESDAY
    DayOfWeek.THURSDAY -> WeekDay.THURSDAY
    DayOfWeek.FRIDAY -> WeekDay.FRIDAY
    DayOfWeek.SATURDAY -> WeekDay.SATURDAY
    DayOfWeek.SUNDAY -> WeekDay.SUNDAY
}