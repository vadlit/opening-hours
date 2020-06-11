package ru.vadlit.openinghours.service

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import ru.vadlit.openinghours.service.TimeFormatter
import java.time.LocalTime

class TimeFormatterTest {

    private val testable = TimeFormatter()

    @Test
    fun hoursAM() {
        // Arrange
        val input = LocalTime.of(1, 0)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "1 AM"
    }

    @Test
    fun hoursPM() {
        // Arrange
        val input = LocalTime.of(13, 0)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "1 PM"
    }

    @Test
    fun moreOrEqual10HoursAM() {
        // Arrange
        val input = LocalTime.of(10, 0)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "10 AM"
    }

    @Test
    fun moreOrEqual10HoursPM() {
        // Arrange
        val input = LocalTime.of(22, 0)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "10 PM"
    }

    @Test
    fun minutesAM() {
        // Arrange
        val input = LocalTime.of(11, 12)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "11:12 AM"
    }

    @Test
    fun minutesPM() {
        // Arrange
        val input = LocalTime.of(23, 12)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "11:12 PM"
    }

    @Test
    fun less10MinutesAM() {
        // Arrange
        val input = LocalTime.of(11, 9)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "11:09 AM"
    }

    @Test
    fun less10MinutesPM() {
        // Arrange
        val input = LocalTime.of(23, 9)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "11:09 PM"
    }

    @Test
    fun midnight() {
        // Arrange
        val input = LocalTime.of(0, 1)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "12:01 AM"
    }

    @Test
    fun afternoon() {
        // Arrange
        val input = LocalTime.of(12, 1)

        // Act
        val output = testable.format12Hours(input)

        // Assert
        output shouldBeEqualTo "12:01 PM"
    }
}