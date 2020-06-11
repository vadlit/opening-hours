package ru.vadlit.openinghours.service

import org.amshove.kluent.`should be`
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import ru.vadlit.openinghours.domain.EventType
import ru.vadlit.openinghours.domain.ScheduleEvent
import ru.vadlit.openinghours.exceptions.ClosedOnlyException
import ru.vadlit.openinghours.exceptions.DuplicatedTypeException
import ru.vadlit.openinghours.exceptions.NotClosedException
import ru.vadlit.openinghours.service.HoursFormatter
import ru.vadlit.openinghours.service.TimeFormatter
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertFailsWith

/**
 * Integration tests for HoursFormatter and TimeFormatter
 */
class HoursFormatterIT {

    private val timeFormatter = TimeFormatter()
    private val testable = HoursFormatter(timeFormatter)

    @Test
    fun formatOpenOnly() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0))
            )
        )

        // Act & Assert
        assertFailsWith(NotClosedException::class) { testable.format(input) }
    }

    @Test
    fun formatCloseOnly() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0))
            )
        )

        // Act & Assert
        assertFailsWith(ClosedOnlyException::class) { testable.format(input) }
    }

    @Test
    fun formatOpenOnlyButOnTheNextDayDoesntWork() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0))
            ),
            DayOfWeek.TUESDAY to emptyList()
        )

        // Act & Assert
        assertFailsWith(NotClosedException::class) { testable.format(input) }
    }

    @Test
    fun formatClosedOnlyWithoutOpening() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            ),
            DayOfWeek.TUESDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0))
            )
        )

        // Act & Assert
        assertFailsWith(DuplicatedTypeException::class) { testable.format(input) }
    }

    @Test
    fun formatOpenedOnlyWithoutClosing() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0))
            ),
            DayOfWeek.TUESDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            )
        )

        // Act & Assert
        assertFailsWith(DuplicatedTypeException::class) { testable.format(input) }
    }

    @Test
    fun formatEmpty() {
        // Arrange
        val input = emptyMap<DayOfWeek, List<ScheduleEvent>>()

        // Act
        val output = testable.format(input)

        // Assert
        output.shouldBeEmpty()
    }

    @Test
    fun formatClosedFullDay() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to emptyList<ScheduleEvent>()
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 1
        val daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "Closed"
    }

    @Test
    fun formatOneDay() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 1
        val daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 11 PM"
    }

    @Test
    fun formatOneDayWithReversedOrderOfEvents() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 1
        val daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 11 PM"
    }

    @Test
    fun formatTwoDays() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            ),
            DayOfWeek.TUESDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 1
        val daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"
    }

    @Test
    fun formatTwoDaysAndAgainTwoDays() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            ),
            DayOfWeek.TUESDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(15, 0))
            ),
            DayOfWeek.WEDNESDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 2
        var daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"

        daySchedule = output[DayOfWeek.TUESDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "3 PM - 9 AM"
    }

    @Test
    fun formatFullWeekWithNightWorks() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            ),
            DayOfWeek.TUESDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            ),
            DayOfWeek.WEDNESDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            ),
            DayOfWeek.THURSDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            ),
            DayOfWeek.FRIDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            ),
            DayOfWeek.SATURDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            ),
            DayOfWeek.SUNDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(23, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 7
        var daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"

        daySchedule = output[DayOfWeek.TUESDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"

        daySchedule = output[DayOfWeek.WEDNESDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"

        daySchedule = output[DayOfWeek.THURSDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"

        daySchedule = output[DayOfWeek.FRIDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"

        daySchedule = output[DayOfWeek.SATURDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"

        daySchedule = output[DayOfWeek.SUNDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "11 PM - 9 AM"
    }

    @Test
    fun formatMultipleIntervals() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(13, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(15, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 1
        val daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 1 PM, 3 PM - 11 PM"
    }

    @Test
    fun formatTwoDaysWithMultipleIntervalsOnFirstDay() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(13, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(15, 0))
            ),
            DayOfWeek.TUESDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(13, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 1
        val daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 1 PM, 3 PM - 1 PM"
    }

    @Test
    fun formatTwoDaysWithMultipleIntervalsOnSecondDay() {
        // Arrange
        val input = mapOf(
            DayOfWeek.FRIDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(18, 0))
            ),
            DayOfWeek.SATURDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(1, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(11, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(16, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 2
        var daySchedule = output[DayOfWeek.FRIDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "6 PM - 1 AM"

        daySchedule = output[DayOfWeek.SATURDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 11 AM, 4 PM - 11 PM"
    }

    @Test
    fun formatAbsentDays() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            ),
            DayOfWeek.WEDNESDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 2
        var daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 11 PM"

        daySchedule = output[DayOfWeek.WEDNESDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 11 PM"
    }

    @Test
    fun formatWithClosedDays() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            ),
            DayOfWeek.TUESDAY to listOf(),
            DayOfWeek.WEDNESDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(9, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(23, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 3
        var daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 11 PM"

        daySchedule = output[DayOfWeek.TUESDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "Closed"

        daySchedule = output[DayOfWeek.WEDNESDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "9 AM - 11 PM"
    }

    @Test
    fun formatFullWeek() {
        // Arrange
        val input = mapOf(
            DayOfWeek.MONDAY to listOf(),
            DayOfWeek.TUESDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(10, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(18, 0))
            ),
            DayOfWeek.WEDNESDAY to listOf(),
            DayOfWeek.THURSDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(10, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(18, 0))
            ),
            DayOfWeek.FRIDAY to listOf(
                ScheduleEvent(EventType.OPEN, LocalTime.of(10, 0))
            ),
            DayOfWeek.SATURDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(1, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(10, 0))
            ),
            DayOfWeek.SUNDAY to listOf(
                ScheduleEvent(EventType.CLOSE, LocalTime.of(1, 0)),
                ScheduleEvent(EventType.OPEN, LocalTime.of(12, 0)),
                ScheduleEvent(EventType.CLOSE, LocalTime.of(21, 0))
            )
        )

        // Act
        val output = testable.format(input)

        // Assert
        output.size `should be` 7
        var daySchedule = output[DayOfWeek.MONDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "Closed"

        daySchedule = output[DayOfWeek.TUESDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "10 AM - 6 PM"

        daySchedule = output[DayOfWeek.WEDNESDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "Closed"

        daySchedule = output[DayOfWeek.THURSDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "10 AM - 6 PM"

        daySchedule = output[DayOfWeek.FRIDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "10 AM - 1 AM"

        daySchedule = output[DayOfWeek.SATURDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "10 AM - 1 AM"

        daySchedule = output[DayOfWeek.SUNDAY]
        daySchedule shouldNotBe null
        daySchedule!! shouldBeEqualTo "12 PM - 9 PM"
    }
}