package ru.vadlit.openinghours.service

import org.springframework.stereotype.Service
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class TimeFormatter {

    fun format12Hours(time: LocalTime): String {
        if (time.minute == 0) {
            return time.format(formatHours)
        }
        return time.format(formatHoursAndMinutes)
    }

    private companion object {
        private val formatHours = DateTimeFormatter.ofPattern("h a");
        private val formatHoursAndMinutes = DateTimeFormatter.ofPattern("h:mm a");
    }
}