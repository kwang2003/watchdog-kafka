package com.pachiraframework.watchdog.kafka.consumer.data.hadler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pachiraframework.watchdog.kafka.consumer.data.LogMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常日志处理器
 * @author wangxuzheng
 *
 */
@Slf4j
@Component
public class ExceptionLogMessageHandler extends AbstractLogMessageHandler {
	private static final String EXCEPTION_TOPIC = "exception-topic";
	private static final Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");
	private Gson gson = new GsonBuilder().create();
	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;
	@Override
	protected void doHandle(LogMessage logMessage) {
		log.info("found exception,host={},appId={}",logMessage.getHost(),logMessage.getAppId());
		String json = gson.toJson(logMessage);
		kafkaTemplate.send(EXCEPTION_TOPIC, json);
	}

	@Override
	protected boolean match(LogMessage logMessage) {
		String message = Strings.nullToEmpty(logMessage.getMessage());
		Matcher m = CRLF.matcher(message);
		return m.find() && message.contains("Exception");
	}

}
