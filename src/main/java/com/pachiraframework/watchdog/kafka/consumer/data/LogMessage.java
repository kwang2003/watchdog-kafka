package com.pachiraframework.watchdog.kafka.consumer.data;

import lombok.Builder;
import lombok.Data;

/**
 * 消息记录
 * @author wangxuzheng
 *
 */
@Data
@Builder
public class LogMessage {
	/**
	 * 日志消息内容
	 */
	private String message;
	/**
	 * 发生时间
	 */
	private String timestamp;
	/**
	 * 日志级别
	 */
	private String level;
	/**
	 * 位置，通常是类名+方法名+行号
	 */
	private String location;
	/**
	 * 所在的主机名或ip
	 */
	private String host;
	/**
	 * 应用
	 */
	private String appId;
}
