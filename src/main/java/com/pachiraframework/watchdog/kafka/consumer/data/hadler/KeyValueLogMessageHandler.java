package com.pachiraframework.watchdog.kafka.consumer.data.hadler;

import org.springframework.stereotype.Component;

import com.pachiraframework.watchdog.kafka.consumer.data.LogMessage;

/**
 * @author wangxuzheng
 *
 */
@Component
public class KeyValueLogMessageHandler extends AbstractLogMessageHandler {

	@Override
	protected void doHandle(LogMessage logMessage) {

	}

	@Override
	protected boolean match(LogMessage logMessage) {
		return false;
	}

}
