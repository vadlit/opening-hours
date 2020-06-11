package ru.vadlit.openinghours.mock

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import ru.vadlit.openinghours.OpeningHoursApplication

@ComponentScan(
    basePackageClasses = [OpeningHoursApplication::class]
)
@EnableWebMvc
@EnableConfigurationProperties
class MockTestConfiguration