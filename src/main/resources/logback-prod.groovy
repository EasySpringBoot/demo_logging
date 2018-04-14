import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import java.nio.charset.Charset

import static ch.qos.logback.classic.Level.ERROR

def USER_HOME = System.getProperty("user.home")
def APP_NAME = "demo_logging"
def LOG_PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} - %msg %n"
def LOG_FILE = "${USER_HOME}/logs/${APP_NAME}"
def FILE_NAME_PATTERN = "${APP_NAME}.%d{yyyy-MM-dd}.log"

scan("60 seconds")

context.name = "${APP_NAME}"
jmxConfigurator()

logger("org.springframework.web", ERROR)
logger("com.easy.springboot.demo_logging", ERROR)

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "${LOG_PATTERN}"
        charset = Charset.forName("utf8")
    }
}

appender("dailyRollingFileAppender", RollingFileAppender) {
    file = "${LOG_FILE}"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${FILE_NAME_PATTERN}"
        maxHistory = 30
    }
    filter(ThresholdFilter) {
        level = ERROR
    }
    encoder(PatternLayoutEncoder) {
        pattern = "${LOG_PATTERN}"
    }
}
root(ERROR, ["CONSOLE", "dailyRollingFileAppender"])