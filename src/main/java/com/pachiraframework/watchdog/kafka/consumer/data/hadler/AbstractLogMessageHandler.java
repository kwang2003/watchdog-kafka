package com.pachiraframework.watchdog.kafka.consumer.data.hadler;

import com.pachiraframework.watchdog.kafka.consumer.data.LogMessage;

/**
 * @author wangxuzheng
 *
 */
public abstract class AbstractLogMessageHandler {
	protected AbstractLogMessageHandler next;
	/**
	 * 设置下一级处理器
	 * @param next
	 */
	public void setNext(AbstractLogMessageHandler next) {
		this.next = next;
	}
	
	/**
	 * 处理消息
	 * @param applicationLog
	 */
	public void handle(LogMessage logMessage) {
		if(match(logMessage)) {
			doHandle(logMessage);
		}
		if(this.next != null) {
			next.handle(logMessage);
		}
	}
	
	protected abstract void doHandle(LogMessage logMessage);
	
	protected abstract boolean match(LogMessage logMessage);
}
