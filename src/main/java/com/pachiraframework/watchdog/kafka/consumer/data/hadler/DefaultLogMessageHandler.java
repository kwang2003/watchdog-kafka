package com.pachiraframework.watchdog.kafka.consumer.data.hadler;

import org.springframework.stereotype.Component;

import com.pachiraframework.watchdog.kafka.consumer.data.LogMessage;

@Component
public class DefaultLogMessageHandler extends AbstractLogMessageHandler {

	@Override
	protected void doHandle(LogMessage logMessage) {

	}

	@Override
	protected boolean match(LogMessage logMessage) {
		return true;
	}

}
