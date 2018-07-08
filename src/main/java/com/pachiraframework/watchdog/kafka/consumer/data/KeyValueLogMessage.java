package com.pachiraframework.watchdog.kafka.consumer.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 程序打点key:value格式的日志
 * @author wangxuzheng
 *
 */
@Getter
@Setter
@ToString(callSuper=true)
public class KeyValueLogMessage extends LogMessage{
	private String key;
	private String value;
}
