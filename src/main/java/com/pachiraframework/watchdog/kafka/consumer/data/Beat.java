package com.pachiraframework.watchdog.kafka.consumer.data;

import lombok.Data;

@Data
public class Beat {
	private String name;
	private String hostname;
	private String version;
}
