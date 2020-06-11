package ru.vadlit.openinghours.api.v1

import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import ru.vadlit.openinghours.mock.MockTestConfiguration

@RunWith(SpringRunner::class)
@SpringBootTest(
    classes = [
        MockServletContext::class,
        MockTestConfiguration::class
    ]
)
@WebAppConfiguration
abstract class BaseIT