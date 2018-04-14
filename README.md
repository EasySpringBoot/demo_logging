# demo_logging

第1章 Spring Boot 日志

在任何一个生产系统中，对日志的合理记录是非常重要的。这对系统故障的定位处理极其关键。Spring Boot支持Java Util Logging,Log4j2,Lockback作为日志框架， Spring Boot使用Logback作为默认日志框架。无论使用哪种日志框架，Spring Boot都支持配置将日志输出到控制台或者文件中。
本章我们来详细介绍 Spring Boot 应用的日志的配置与使用。

1.1 SLF4J与Logback简介

Java日志框架众多，常用的有java.util.logging、log4j、 logback、commons-logging等。本节简单介绍logback日志框架。

1.1.1   SLF4J日志接口

SLF4J (Simple Logging Facade For Java)，它是一个针对于各类Java日志框架的统一Facade抽象。SLF4J定义了统一的日志抽象接口，而真正的日志实现则是在运行时决定。
LogBack是由log4j的创始人开发的新一代日志框架，用于替代log4j。它效率更高、能够适应诸多的运行环境。LogBack的架构设计足够通用，可适用于不同的环境。目前LogBack分为三个模：lobback-core，logback-classic和logback-access。core模块是其它两个模块的基础，classic是core的扩展，是log4j巨大改进的版本。LogBack-classic本身实现了SL4J的API，因此可以很容易的在logback与其它日志系统之间转换，例如log4j、JDK1.4中的java.util.logging（JUL）。第三个模块access，它集成了Servlet容器，提供了通过HTTP访问日志的功能，详细了解access可访问文档: http://logback.qos.ch/access.html。
LogBack的日志级别有trace、debug、info、warn、error，级别排序为：
TRACE < DEBUG < INFO < WARN < ERROR

提示：关于日志级别详细信息，可参考官方文档: http://logback.qos.ch/manual/architecture.html。


一般情况下，我们不需要单独引入spring-boot-starter-logging，因为这是spring-boot-starter默认引入的依赖。其依赖树如下


从上面的依赖树，我们可以看出，spring-boot-starter-logging依赖logback-classic, logback-classic依赖logback-core, sl4j-api。
Spring Boot为我们提供了功能齐全的默认日志配置，基本上就是“开箱即用”。
默认情况下，Spring Boot的日志是输出到控制台的，不写入任何日志文件。
要让Spring Boot输出日志文件，最简单的方式是在application.properties配置文件中配置logging.path键值，如下：
logging.path=${user.home}/logs
这样在${user.home}/logs目录下会生成默认的文件名命名的日志文件spring.log。
我们可以在application.properties配置文件中配置logging.file键值，如下：
spring.application.name=lightsword
logging.file=${user.home}/logs/${spring.application.name}.log
这样日志文件的名字就是lightsword.log了。另外，二者不能同时使用，如同时使用，则只有logging.file生效。

1.2 配置logback日志

Spring Boot 提供了一套日志系统，优先选择logback。日志服务一般都在ApplicationContext创建前就初始化了，所以日志配置，可以独立于Spring的配置。我们也可以通过系统属性和传统的Spring Boot外部配置文件，实现日志控制和管理。

根据不同的日志系统，SpringBoot按如下“约定规则”组织配置文件名加载日志配置文件：
日志框架
配置文件
Logback
logback-spring.xml, logback-spring.groovy, logback.xml, logback.groovy
Log4j
log4j-spring.properties, log4j-spring.xml, log4j.properties, log4j.xml
Log4j2
log4j2-spring.xml, log4j2.xml
JDK (Java Util Logging)
logging.properties

Spring Boot官方推荐优先使用带有-spring的文件名作为你的日志配置（如使用logback-spring.xml，而不是logback.xml），命名为logback-spring.xml的日志配置文件，spring boot可以为它添加一些spring boot特有的配置项。
LogBack读取配置或属性文件的步骤是：

1.  LogBack在类路径下尝试查找logback.groovy的文件。
2.  如果logback.groovy没有找到，就在类路径下查找logback-test.xml文件。
3.  若logback-test.xml文件没有找到，就会在类路径下查找logback.xml文件。

我们也可以自定义logback.xml名称，然后在application.properties中指定它。例如：
#logging
logging.config=classpath:logback-dev.groovy
要把这个logback-dev.groovy配置文件放到类路径下。如下图

我们在application.properties指定环境
spring.profiles.active=daily
对应的application-daily.properties指定日志的配置文件如下
#logging
logging.config=classpath:logback-daily.groovy
另外，如果我们没有配置任何的logback.xml、logback-*.groovy等文件，LogBack就会使用BasicConfigurator启动默认配置，该配置会将日志输出到控制上。这样就意味着使用缺省配置，它提供了默认的最基础的日志功能。

1.3 logback.groovy配置文件

本节介绍 logback 配置文件的具体内容。

1.3.1   显示系统 Log 级别

我们首先编写一个/log 接口来展示当前系统的日志级别。代码如下
```kotlin
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
```
1.3.2   使用logback.groovy配置

领域特定语言（Domain-specific languages ，DSL）用途非常广泛。logback.xml配置文件繁琐而冗长。Groovy是一门优秀的DSL。logback框架支持logback.groovy简洁的DLS风格的配置。详细的配置语法介绍可以参考：https://logback.qos.ch/manual/groovy.html。同时，logback提供了直接把logback.xml转换成logback.groovy的工具:https://logback.qos.ch/translator/asGroovy.html (测试过，这个工具include标签暂时未作解析)。
推荐使用 Groovy DSL 作为logback 日志配置文件的最佳实践。配置logback-daily.groovy如下：

```kotlin
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import java.nio.charset.Charset

import static ch.qos.logback.classic.Level.INFO

def USER_HOME = System.getProperty("user.home")
def APP_NAME = "demo_logging"
def LOG_PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} - %msg %n"
def LOG_FILE = "${USER_HOME}/logs/${APP_NAME}"
def FILE_NAME_PATTERN = "${APP_NAME}.%d{yyyy-MM-dd}.log"

scan("60 seconds")

context.name = "${APP_NAME}"
jmxConfigurator()

logger("org.springframework.web", INFO)
logger("com.easy.springboot.demo_logging", INFO)

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
        level = INFO
    }
    encoder(PatternLayoutEncoder) {
        pattern = "${LOG_PATTERN}"
    }
}
root(INFO, ["CONSOLE", "dailyRollingFileAppender"])
```

上面的 Groovy 配置文件等价于如下的logback-daily.xml配置文件内容：


```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--scan:当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。-->
<!--scanPeriod:设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。-->
<!--debug:当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。-->
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <property name="APP_NAME" value="lightsword"/>
    <contextName>${APP_NAME}</contextName>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <jmxConfigurator/>

    <logger name="org.springframework.web" level="INFO"/>
    <logger name=" com.easy.springboot.demo_logging " level="TRACE"/>

    <appender name="dailyRollingFileAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${user.home}/logs/${APP_NAME}</File>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- daily rolling over -->
            <FileNamePattern>${APP_NAME}.%d{yyyy-MM-dd}.log</FileNamePattern>
            <!-- keep 30 days' log history -->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder>
            <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} - %msg %n</Pattern>
        </encoder>
    </appender>
    <!--TRACE, DEBUG, INFO, WARN, ERROR-->
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
        <!--<appender-ref ref="FILE"/>-->
        <appender-ref ref="dailyRollingFileAppender"/>
    </root>
</configuration>
```

我们可以看出，使用groovy表达的配置，更加简洁、富表现力。只需要在application.properties里面配置logging.config指定日志配置文件即可：

#logging
logging.config=classpath:logback-dev.groovy

另外，需要在build.gradle中添加groovy依赖：

dependencies {
    ...
    compile group: 'org.codehaus.groovy', name: 'groovy-all', version: '2.4.15'
}

完成上述配置，即可使用跟logback.xml配置一样的日志功能了。

1.3.3   配置文件说明

在logback.xml形式配置文件内，总体结构是：最顶层是一个<configuration>标签，在<configuration>标签下可以有0到n个<appender>标签，0到n个<logger>标签，最多只能有1个<root>标签，以及其他一些高级配置。下面我们针对上面的logback.xml配置文件作简要说明。

1.configuration 节点
配置文件中的根节点中的<configuration scan="true" scanPeriod="60 seconds" debug="false"> 包含的属性简单说明如下：
   scan:当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
   scanPeriod:设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。
   debug:当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。


2.jmxConfigurator

<jmxConfigurator/>标签对应 Groovy 配置脚本中的jmxConfigurator()。这个配置是开启JMX的功能。JMX（Java Management Extensions，即Java管理扩展）是一个为应用程序、设备、系统等植入管理功能的框架。JMX可以跨越一系列异构操作系统平台、系统体系结构和网络传输协议，灵活的开发无缝集成的系统、网络和服务管理应用。
有了这个配置，我们可以直接在命令行输入：jconsole ，这个命令会启动jconsole的GUI界面。如下图


点击到MBeans Tab，我们可以看到关于logback的信息：

启动系统，采用spring.profiles.active=daily配置，日志级别是
logger("com.easy.springboot.demo_logging", INFO)
访问http://127.0.0.1:8080/log ，响应输出：“3-INFO|2-WARN|1-ERROR|”。
然后，点击“setLoggerLevel”,我们可以在设置界面动态修改系统的日志级别：


设置 p1参数为：com.easy.springboot.demo_logging，设置 p2参数为: TRACE，点击“setLoggerLevel” 按钮，提示“Method successfully invoked”，如下图：


再次访问http://127.0.0.1:8080/log ，响应输出：“5-TRACE|4-DEBUG|3-INFO|2-WARN|1-ERROR|”。我们可以看出系统的日志级别已经变成了TRACE 。

提示：关于jconsole的详细介绍, 可以参考：https://docs.oracle.com/javase/8/docs/technotes/guides/management/jconsole.html。


3.Logger节点

在配置<logger name="org.springframework.web" level="INFO"/>中，我们定义了一个 捕获 org.springframework.web 的日志，日志级别是 DEBUG。一个捕获com.springboot.in.action的日志，日志级别是TRACE。
上面引用的org/springframework/boot/logging/logback/base.xml 文件是SpringBoot内置的，其内容为：

<?xml version="1.0" encoding="UTF-8"?>
<included>
    <include resource="org/springframework/boot/logging/logback/defaults.xml" />
    <property name="LOG_FILE" value="${LOG_FILE:-${LOG_PATH:-${LOG_TEMP:-${java.io.tmpdir:-/tmp}}/}spring.log}"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
    <include resource="org/springframework/boot/logging/logback/file-appender.xml" />
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</included>

其中，引用的defaults.xml，console-appender.xml，file-appender.xml都在同一个目录下。默认情况下包含两个appender——一个是控制台，一个是文件，分别定义在console-appender.xml和file-appender.xml中。这里面的内容就是SpringBoot默认实现的logback的日志配置。
Spring Boot的日志模块里，预定义了一些系统变量：
   PID，当前进程ID
   LOG_FILE，Spring Boot配置文件中logging.file的值
   LOG_PATH, Spring Boot配置文件中logging.path的值
   CONSOLE_LOG_PATTERN， Spring Boot配置文件中logging.pattern.console的值
   FILE_LOG_PATTERN， Spring Boot配置文件中logging.pattern.file的值。
对于应用的日志级别也可以通过application.properties进行定义：
logging.level.org.springframework.web=DEBUG
这样相当于我们在logback.xml 中配置的对应的日志级别。名称以logging.level开头，后面跟要输入日志的包名。
另外，如果在 logback.xml 和 application.properties 中定义了相同的配置（如都配置了 org.springframework.web）但是输出级别不同，由于application.properties 的优先级高于 logback.xml ，所以会使用application.properties的配置。

4. ConsoleAppender

Logback使用appender来定义日志输出，在开发过程中最常用的是将日志输出到控制台。我们直接使用SpringBoot内置的ConsoleAppender配置。这个配置的内容如下：

<?xml version="1.0" encoding="UTF-8"?>
<included>
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
    <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
    <conversionRule conversionWord="wEx" converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />
    <property name="CONSOLE_LOG_PATTERN" value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>
    <property name="FILE_LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} ${LOG_LEVEL_PATTERN:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}"/>

    <appender name="DEBUG_LEVEL_REMAPPER" class="org.springframework.boot.logging.logback.LevelRemappingAppender">
        <destinationLogger>org.springframework.boot</destinationLogger>
    </appender>

    <logger name="org.apache.catalina.startup.DigesterFactory" level="ERROR"/>
    <logger name="org.apache.catalina.util.LifecycleBase" level="ERROR"/>
    <logger name="org.apache.coyote.http11.Http11NioProtocol" level="WARN"/>
    <logger name="org.apache.sshd.common.util.SecurityUtils" level="WARN"/>
    <logger name="org.apache.tomcat.util.net.NioSelectorPool" level="WARN"/>
    <logger name="org.crsh.plugin" level="WARN"/>
    <logger name="org.crsh.ssh" level="WARN"/>
    <logger name="org.eclipse.jetty.util.component.AbstractLifeCycle" level="ERROR"/>
    <logger name="org.hibernate.validator.internal.util.Version" level="WARN"/>
    <logger name="org.springframework.boot.actuate.autoconfigure.CrshAutoConfiguration" level="WARN"/>
    <logger name="org.springframework.boot.actuate.endpoint.jmx" additivity="false">
        <appender-ref ref="DEBUG_LEVEL_REMAPPER"/>
    </logger>
    <logger name="org.thymeleaf" additivity="false">
        <appender-ref ref="DEBUG_LEVEL_REMAPPER"/>
    </logger>
</included>
<included>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>
</included>

其中，
   charset，表示对日志进行编码
   pattern简单说明：
   %d{HH:mm:ss.SSS}——日志输出时间
   %thread——输出日志的进程名字，方括号括起来。这个信息在Web应用以及异步任务处理中很有用。
   %-5level——日志级别，并且使用5个字符靠左对齐
   %logger{36}——日志输出者的名字
   %msg——日志消息
   %n——平台的换行符
在这种格式下一条日志的输出内容格式如下：

02:37:22.752 [http-nio-8888-exec-1] DEBUG o.s.s.w.a.AnonymousAuthenticationFilter - Populated SecurityContextHolder with anonymous token: 'org.springframework.security.authentication.AnonymousAuthenticationToken@6fab4e5e: Principal: anonymousUser; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@fffe3f86: RemoteIpAddress: 127.0.0.1; SessionId: E30F2AF513F94C7FC7611353B61A26C6; Granted Authorities: ROLE_ANONYMOUS'

5.RollingFileAppender

另一种通用功能是将日志输出到文件。同时，随着应用的运行时间越来越长，日志也会增长的越来越多，将他们输出到同一个文件并非一个好办法。我们有RollingFileAppender用于切分文件日志：

<appender name="dailyRollingFileAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${user.home}/logs/${APP_NAME}</File>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- daily rolling over -->
            <FileNamePattern>${APP_NAME}.%d{yyyy-MM-dd}.log</FileNamePattern>
            <!-- keep 30 days' log history -->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} - %msg %n</Pattern>
        </encoder>
</appender>

其中，核心的配置部分是是rollingPolicy的定义。上面的

<FileNamePattern>${APP_NAME}.%d{yyyy-MM-dd}.log</FileNamePattern>
<maxHistory>30</maxHistory>

其中，
   FileNamePattern，定义了日志的切分方式——把每一天的日志归档到一个文件中。
   maxHistory，30表示只保留最近30天的日志，以防止日志填满整个磁盘空间。我们也可以使用%d{yyyy-MM-dd_HH-mm}来定义精确到分的日志切分方式。

6.Threshold filter

ThresholdFilter是 logback定义的日志打印级别的过滤器。例如配置ThresholdFilter来过滤掉ERROR级别以下的日志不输出到文件中：


<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
    <level>ERROR</level>
</filter>

7.root节点

关于 root 节点的配置如下：

<root level="DEBUG">
    <appender-ref ref="CONSOLE"/>
    <!--<appender-ref ref="FILE"/>-->
    <appender-ref ref="dailyRollingFileAppender"/>
</root>

root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性。level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，不能设置为INHERITED或者同义词NULL。默认是DEBUG。

1.4 本章小结

Spring Boot 集成logback日志框架非常简单。同时，使用基于 Groovy DSL的 logback.groovy 配置文件，风格简洁优雅。使用 spring.profile 配置多环境（dev、daily、prod 等）的日志配置文件也非常简单方便。通过配置jmxConfigurator我们可以在 jconsole 管理后台动态修改系统的日志级别。

提示：本章实例工程源代码 https://github.com/EasySpringBoot/demo_logging
