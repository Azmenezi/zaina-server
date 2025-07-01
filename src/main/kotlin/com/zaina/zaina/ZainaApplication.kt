package com.zaina.zaina

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.socket.config.annotation.EnableWebSocket

@SpringBootApplication
@EnableAsync
@EnableWebSocket
class ZainaApplication

fun main(args: Array<String>) {
	runApplication<ZainaApplication>(*args)
}
