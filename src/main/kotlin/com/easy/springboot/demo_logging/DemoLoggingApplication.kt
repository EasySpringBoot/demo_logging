package com.easy.springboot.demo_logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DemoLoggingApplication

fun main(args: Array<String>) {
    runApplication<DemoLoggingApplication>(*args)
}
