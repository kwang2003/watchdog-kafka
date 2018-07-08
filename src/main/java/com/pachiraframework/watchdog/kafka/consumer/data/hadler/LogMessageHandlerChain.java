package com.pachiraframework.watchdog.kafka.consumer.data.hadler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pachiraframework.watchdog.kafka.consumer.data.LogMessage;

/**
 * @author wangxuzheng
 *
 */
@Component
public class LogMessageHandlerChain implements InitializingBean{
	@Autowired
	private ExceptionLogMessageHandler exceptionLogMessageHandler;
	@Autowired
	private KeyValueLogMessageHandler keyValueLogMessageHandler;
	@Autowired
	private DefaultLogMessageHandler defaultLogMessageHandler;
	public void handle(LogMessage applicationLog) {
		exceptionLogMessageHandler.handle(applicationLog);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		exceptionLogMessageHandler.setNext(keyValueLogMessageHandler);
		keyValueLogMessageHandler.setNext(defaultLogMessageHandler);
	}
}
