package com.pachiraframework.watchdog.kafka.consumer.data;

import lombok.Data;

@Data
public class Metadata {
	private String beat;
	private String type;
	private String version;
	private String topic;
}
