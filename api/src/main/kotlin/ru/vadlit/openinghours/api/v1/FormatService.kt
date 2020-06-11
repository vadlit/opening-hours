package ru.vadlit.openinghours.api.v1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.vadlit.openinghours.api.v1.message.FormatRequest
import ru.vadlit.openinghours.api.v1.message.FormatResponse
import javax.validation.Valid

interface FormatService {
    @GetMapping(value = ["/api/v1/format"])
    fun format(@RequestBody @Valid request: FormatRequest): FormatResponse
}