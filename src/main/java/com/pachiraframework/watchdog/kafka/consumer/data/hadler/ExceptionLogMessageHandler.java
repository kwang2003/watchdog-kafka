package com.pachiraframework.watchdog.kafka.consumer.data.hadler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.pachiraframework.watchdog.kafka.consumer.data.LogMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExceptionLogMessageHandler extends AbstractLogMessageHandler {
	private static final Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");

	@Override
	protected void doHandle(LogMessage logMessage) {
		log.info("{}", logMessage);
	}

	@Override
	protected boolean match(LogMessage logMessage) {
		String message = Strings.nullToEmpty(logMessage.getMessage());
		Matcher m = CRLF.matcher(message);
		return m.find() && message.contains("Exception");
	}

}
