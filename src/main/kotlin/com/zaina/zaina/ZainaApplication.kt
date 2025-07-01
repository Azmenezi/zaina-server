package com.zaina.zaina

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableAsync
@EnableWebSocket
@EnableJpaAuditing
class ZainaApplication

fun main(args: Array<String>) {
	runApplication<ZainaApplication>(*args)
}
