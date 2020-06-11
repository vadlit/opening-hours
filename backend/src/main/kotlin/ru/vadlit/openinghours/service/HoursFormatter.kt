package ru.vadlit.openinghours.service

import mu.KLogging
import org.springframework.stereotype.Service
import ru.vadlit.openinghours.domain.EventType
import ru.vadlit.openinghours.domain.ScheduleEvent
import ru.vadlit.openinghours.exceptions.ClosedOnlyException
import ru.vadlit.openinghours.exceptions.DuplicatedTypeException
import ru.vadlit.openinghours.exceptions.NotClosedException
import java.lang.IllegalArgumentException
import java.time.DayOfWeek

@Service
class HoursFormatter(
    private val timeFormatter: TimeFormatter
) {

    fun format(input: Map<DayOfWeek, List<ScheduleEvent>>): Map<DayOfWeek, String> {
        logger.info { "Formatting $input" }

        val sortedDays = input.keys.asSequence().sorted()

        val sortedDayToEvent = sortedDays
            .flatMap { dayOfWeek ->
                val events = input.getValue(dayOfWeek).asSequence()
                events
                    .sortedBy { it.time }
                    .map { dayOfWeek to it }
                    .ifEmpty {
                        // closed all the day
                        sequenceOf(dayOfWeek to null)
                    }
            }
            .toList()

        val result = mutableMapOf<DayOfWeek, String>()
        if (sortedDayToEvent.isEmpty()) {
            return result
        }

        var state: EventType? = null
        var closeTimeOfLastDayStr: String? = null
        sortedDayToEvent.mapIndexed { index, (dayOfWeek, event) ->
            if (event == null) {
                if (state == EventType.OPEN) {
                    val previousDay = sortedDayToEvent[index - 1].first
                    throw NotClosedException(previousDay)
                }
                result[dayOfWeek] = CLOSED_STR
            } else {
                if (event.type == state) {
                    throw DuplicatedTypeException(dayOfWeek, event.type)
                }
                state = event.type
                val timeStr = timeFormatter.format12Hours(event.time)

                when (state) {
                    EventType.OPEN -> {
                        result.compute(dayOfWeek) { _, old ->
                            if (old == null) timeStr else "$old, $timeStr"
                        }
                    }
                    EventType.CLOSE -> {
                        var old = result[dayOfWeek]
                        if (old == null) {
                            if (index > 0) {
                                val previousDay = sortedDayToEvent[index - 1].first
                                old = result[previousDay]
                                result[previousDay] = "$old - $timeStr"
                            } else {
                                closeTimeOfLastDayStr = timeStr
                            }
                        } else {
                            result[dayOfWeek] = "$old - $timeStr"
                        }
                    }
                    else -> throw IllegalArgumentException("Unexpected state $state")
                }
            }
        }

        if (closeTimeOfLastDayStr != null) {
            val (lastDay, lastEvent) = sortedDayToEvent.last()
            if (lastEvent == null || lastEvent.type == EventType.CLOSE) {
                if (sortedDayToEvent.size <= 1) {
                    throw ClosedOnlyException()
                }
                val firstDay = sortedDayToEvent.first().first
                throw DuplicatedTypeException(firstDay, EventType.CLOSE)
            }

            val old = result.getValue(lastDay)
            result[lastDay] = "$old - $closeTimeOfLastDayStr"
        } else if (state == EventType.OPEN) {
            val lastDay = sortedDayToEvent.last().first
            throw NotClosedException(lastDay)
        }

        return result
    }

    private companion object : KLogging() {
        private const val CLOSED_STR = "Closed"
    }
}