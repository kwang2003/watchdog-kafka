package com.pachiraframework.watchdog.kafka.consumer.data;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 应用日志数据封装
 * @author wangxuzheng
 *
 */
@Data
public class FileBeatLogMessage {
	@SerializedName("@timestamp")
	private String timestamp;
	@SerializedName("@metadata")
	private Metadata metadata;
	private Beat beat;
	private String source;
	private Long offset;
	private String message;
//	private Fields fields;
	private Map<String, String> fields;
}
