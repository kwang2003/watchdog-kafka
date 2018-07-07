package com.pachiraframework.watchdog.kafka.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pachiraframework.watchdog.kafka.consumer.data.ApplicationLog;
import com.pachiraframework.watchdog.kafka.consumer.data.LogMessage;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用日志监听，日志中的log4j.xml配置如下：
 * <pre>
 * {@code
 * 	<appender name="file-log" class="org.apache.log4j.DailyRollingFileAppender">
 * 		<param name="file" value="order-service.log" />
 * 		<param name="append" value="true" />
 * 		<param name="DatePattern" value="'.'yyyy-MM-dd" />
 * 		<layout class="org.apache.log4j.PatternLayout">
 * 			<param name="ConversionPattern" value="[%t] %d %-5p [%c.%M(%L)] %m%n" />
 * 		</layout>
 * 	</appender>
 *  }
 *  </pre>
 *  日志示例：<br/>
 *  <pre>
 *  [main] 2018-07-07 10:31:12,138 INFO  [org.springframework.context.support.ClassPathXmlApplicationContext.prepareRefresh(503)] Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@7e9a5fbe: startup date [Sat Jul 07 10:31:12 CST 2018]; root of context hierarchy
 *  </pre>
 *  topic中的数据示例：<br/>
 *  <pre>
 *  {"@timestamp":"2018-07-07T02:46:46.780Z","@metadata":{"beat":"filebeat","type":"doc","version":"6.3.0","topic":"lxw1234"},"prospector":{"type":"log"},"input":{"type":"log"},"beat":{"name":"01AD58697812703","hostname":"01AD58697812703","version":"6.3.0"},"host":{"name":"01AD58697812703"},"source":"D:\\home\\admin\\output\\logs\\javalog\\order-service.log","offset":1366406,"message":"[main] 2018-07-07 10:46:41,641 INFO  [org.springframework.context.support.ClassPathXmlApplicationContext.prepareRefresh(503)] Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@7e9a5fbe: startup date [Sat Jul 07 10:46:41 CST 2018]; root of context hierarchy"}
 *  </pre>
 * @author kevin wang
 */
@Slf4j
@Component
public class ApplicationLogConsumer extends AbstractKafkaConsumer {
	private Grok applicationLogGrok = applicationLogGrok();
	private Gson gson = new GsonBuilder().create();
	@KafkaListener(topics = "lxw1234")
	public void listen(ConsumerRecord<Integer, String> cr) throws Exception {
		int partition = cr.partition();
		log.info("partition={}",partition);
		String message = cr.value();
		ApplicationLog applicationLog = gson.fromJson(message, ApplicationLog.class);
		Match match = applicationLogGrok.match(applicationLog.getMessage());
		final Map<String, Object> capture = match.capture();
		LogMessage logMessage = buildLogMessage(capture);
		logMessage.setHost(applicationLog.getFields()==null?applicationLog.getBeat().getHostname():applicationLog.getFields().getIp());
		logMessage.setAppId(applicationLog.getFields()== null?"NOT_PROVIDED":applicationLog.getFields().getAppId());
		log.info("{}",logMessage);
	}
	
	
	private LogMessage buildLogMessage(Map<String, Object> capture) {
		String level = (String)capture.get("level");
		String message = (String)capture.get("message");
		String timestamp = (String)capture.get("timestamp");
		String location = (String)capture.get("location");
		LogMessage logMessage = LogMessage.builder().level(level).location(location).timestamp(timestamp)
				.message(message).build();
		return logMessage;
	}
	
	@SneakyThrows
	private Grok applicationLogGrok(){
		/* Create a new grokCompiler instance */
		GrokCompiler grokCompiler = GrokCompiler.newInstance();
		grokCompiler.registerDefaultPatterns();
		grokCompiler.registerPatternFromClasspath("/patterns/watchdog");

		/* Grok pattern to compile, here httpd logs */
		final Grok grok = grokCompiler.compile("\\[%{NOTSPACE:thread}\\]%{SPACE}%{TIMESTAMP_ISO8601:timestamp}%{SPACE}%{LOGLEVEL:level}%{SPACE}\\[%{NOTSPACE:location}\\]%{SPACE}%{GREEDYDATA:message}");
		return grok;
	}
}
