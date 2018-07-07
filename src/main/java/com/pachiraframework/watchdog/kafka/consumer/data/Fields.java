package com.pachiraframework.watchdog.kafka.consumer.data;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Fields {
	@SerializedName("app_id")
	private String appId;
	private String ip;
}
