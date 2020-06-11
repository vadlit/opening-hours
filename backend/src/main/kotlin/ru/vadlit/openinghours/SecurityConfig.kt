package ru.vadlit.openinghours

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import ru.vadlit.platform.security.server.WebSecurityConfigurer

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurer() {
    override fun config(http: HttpSecurity) {
        http.csrf().disable().authorizeRequests()
            .antMatchers(HttpMethod.GET, "/api/v1/format").permitAll()
            .antMatchers("/management/**").permitAll()
    }
}