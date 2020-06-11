package ru.vadlit.openinghours.api.v1

import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.nio.charset.Charset

/**
 * Integration tests for FormatService REST-controller
 */
class FormatServiceImplIT : BaseIT() {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @Before
    fun before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    fun succeeded() {
        // Arrange
        val requestBody = clazz.getResource("/formatServiceImplIT/format_request.json").readText()
        val expectedResponse = clazz.getResource("/formatServiceImplIT/format_response.json").readText()

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/format")
                .contentType(contentType)
                .content(requestBody)
                .accept(contentType)
        )
            // Assert
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(content().json(expectedResponse))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }

    @Test
    fun invalidJson() {
        // Arrange
        val requestBody = "something_invalid"

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/format")
                .contentType(contentType)
                .content(requestBody)
                .accept(contentType)
        )
            // Assert
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }

    @Test
    fun noValueInScheduleEvent() {
        // Arrange
        val requestBody = clazz.getResource("/formatServiceImplIT/format_no_value_request.json").readText()

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/format")
                .contentType(contentType)
                .content(requestBody)
                .accept(contentType)
        )
            // Assert
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }

    @Test
    fun noTypeInScheduleEvent() {
        // Arrange
        val requestBody = clazz.getResource("/formatServiceImplIT/format_no_type_request.json").readText()

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/format")
                .contentType(contentType)
                .content(requestBody)
                .accept(contentType)
        )
            // Assert
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }

    @Test
    fun negativeValueInScheduleEvent() {
        // Arrange
        val requestBody = clazz.getResource("/formatServiceImplIT/format_negative_value_request.json").readText()

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/format")
                .contentType(contentType)
                .content(requestBody)
                .accept(contentType)
        )
            // Assert
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }

    @Test
    fun tooBigValueInScheduleEvent() {
        // Arrange
        val requestBody = clazz.getResource("/formatServiceImplIT/format_too_big_value_request.json").readText()

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/format")
                .contentType(contentType)
                .content(requestBody)
                .accept(contentType)
        )
            // Assert
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }

    @Test
    fun invalidSchedule() {
        // Arrange
        val requestBody = clazz.getResource("/formatServiceImplIT/format_invalid_schedule_request.json").readText()

        // Act
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/format")
                .contentType(contentType)
                .content(requestBody)
                .accept(contentType)
        )
            // Assert
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andReturn()
    }

    private companion object {
        private val contentType = MediaType(
            MediaType.APPLICATION_JSON.type,
            MediaType.APPLICATION_JSON.subtype,
            Charset.forName("utf8")
        )

        private val clazz = FormatServiceImplIT::class.java
    }
}