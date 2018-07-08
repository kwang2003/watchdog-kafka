package com.pachiraframework.watchdog.kafka.consumer.data;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 自定义字段封装
 * @author wangxuzheng
 *
 */
@Data
public class Fields {
	@SerializedName("app_id")
	private String appId;
	private String ip;
}
