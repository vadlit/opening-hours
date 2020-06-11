package ru.vadlit.openinghours

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class OpeningHoursApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(OpeningHoursApplication::class.java, *args)
        }
    }
}