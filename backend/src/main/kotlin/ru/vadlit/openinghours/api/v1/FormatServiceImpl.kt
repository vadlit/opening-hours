package ru.vadlit.openinghours.api.v1

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.vadlit.openinghours.api.v1.message.FormatRequest
import ru.vadlit.openinghours.api.v1.message.FormatResponse
import ru.vadlit.openinghours.api.v1.message.WeekDay
import ru.vadlit.openinghours.converters.toScheduleEvent
import ru.vadlit.openinghours.converters.toWeekDay
import ru.vadlit.openinghours.domain.ScheduleEvent
import ru.vadlit.openinghours.service.HoursFormatter
import java.time.DayOfWeek
import javax.validation.Valid

@RestController
class FormatServiceImpl @Autowired constructor(
    private val hoursFormatter: HoursFormatter
) : FormatService {

    @GetMapping(value = ["/api/v1/format"])
    override fun format(@RequestBody @Valid request: FormatRequest): FormatResponse {
        logger.info { "Handling ${::format.name}: $request" }

        val input = request.toFormatterInput() ?: emptyMap()

        val formattedMap = hoursFormatter.format(input)
        logger.trace { "Formatted map: $formattedMap" }

        val output = formattedMap.toOutput()
        logPretty(output)

        return FormatResponse(output)
    }

    private fun logPretty(output: Map<WeekDay, String>) {
        logger.info {
            "A restaurant is open:\n" +
                output.asSequence()
                    .sortedBy { it.key }
                    .map { (weekDay, scheduleStr) ->
                        "${weekDay.name.toLowerCase().capitalize()}: $scheduleStr"
                    }
                    .joinToString("\n")
        }
    }

    private companion object : KLogging() {
        private fun FormatRequest.toFormatterInput(): Map<DayOfWeek, List<ScheduleEvent>> {
            val map = mutableMapOf<DayOfWeek, List<ScheduleEvent>>()
            monday?.apply { map[DayOfWeek.MONDAY] = map { it.toScheduleEvent() } }
            tuesday?.apply { map[DayOfWeek.TUESDAY] = map { it.toScheduleEvent() } }
            wednesday?.apply { map[DayOfWeek.WEDNESDAY] = map { it.toScheduleEvent() } }
            thursday?.apply { map[DayOfWeek.THURSDAY] = map { it.toScheduleEvent() } }
            friday?.apply { map[DayOfWeek.FRIDAY] = map { it.toScheduleEvent() } }
            saturday?.apply { map[DayOfWeek.SATURDAY] = map { it.toScheduleEvent() } }
            sunday?.apply { map[DayOfWeek.SUNDAY] = map { it.toScheduleEvent() } }
            return map
        }

        private fun Map<DayOfWeek, String>.toOutput(): Map<WeekDay, String> {
            return asSequence().associate { (dayOfWeek, formatted) ->
                dayOfWeek.toWeekDay() to formatted
            }
        }
    }
}