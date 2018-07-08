package com.pachiraframework.watchdog.kafka.consumer.data;

import lombok.Data;

/**
 * Beat对象封装
 * @author wangxuzheng
 *
 */
@Data
public class Beat {
	private String name;
	private String hostname;
	private String version;
}
