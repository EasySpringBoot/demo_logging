package com.easy.springboot.demo_logging

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoggerController {
    val log = LoggerFactory.getLogger(LoggerController::class.java)

    @GetMapping("/log")
    fun log(): String {
        var logLevel = ""

        if (log.isTraceEnabled) {
            log.trace("5-TRACE")
            logLevel += "5-TRACE|"
        }

        if (log.isDebugEnabled) {
            log.debug("4-DEBUG")
            logLevel += "4-DEBUG|"
        }

        if (log.isInfoEnabled) {
            log.info("3-INFO")
            logLevel += "3-INFO|"
        }

        if (log.isWarnEnabled) {
            log.warn("2-WARN")
            logLevel += "2-WARN|"
        }

        if (log.isErrorEnabled) {
            log.error("1-ERROR")
            logLevel += "1-ERROR|"
        }
        return logLevel
    }
}

//LogBack的日志级别有trace、debug、info、warn、error，级别排序为：
//TRACE < DEBUG < INFO < WARN < ERROR
//提示：关于日志级别详细信息，可参考官方文档: http://logback.qos.ch/manual/architecture.html。