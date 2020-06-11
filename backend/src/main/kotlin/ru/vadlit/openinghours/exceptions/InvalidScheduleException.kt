package ru.vadlit.openinghours.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import ru.vadlit.openinghours.domain.EventType
import ru.vadlit.platform.web.exceptions.DetailedRuntimeException
import java.time.DayOfWeek

@ResponseStatus(HttpStatus.BAD_REQUEST)
sealed class InvalidScheduleException(description: String) : DetailedRuntimeException(
    mapOf("description" to description)
) {

    override val message: String?
        get() = "Invalid schedule: {description}"
}

class ClosedOnlyException : InvalidScheduleException("Closed only")

class NotClosedException(dayOfWeek: DayOfWeek) : InvalidScheduleException("Not closed after $dayOfWeek")

class DuplicatedTypeException(dayOfWeek: DayOfWeek, type: EventType) : InvalidScheduleException("Duplicated $type on $dayOfWeek")