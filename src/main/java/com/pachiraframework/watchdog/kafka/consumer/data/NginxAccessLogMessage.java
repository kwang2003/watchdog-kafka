package com.pachiraframework.watchdog.kafka.consumer.data;

import lombok.Data;

/**
 * Nginx访问日志信息
 * @author wangxuzheng
 *
 */
@Data
public class NginxAccessLogMessage {
	/**
	 * nginx访问日志对应的域名
	 */
	private String domain;
	/**
	 * 发生在哪个主机上
	 */
	private String host;
	/**
	 * HTTP状态码
	 */
	private Integer httpStatus;
	/**
	 * 请求方法，GET/POST/PUT/DELETE
	 */
	private String httpMethod;
	/**
	 * HTTP/1.1
	 */
	private String httpVersion;
	/**
	 * http user
	 */
	private String user;
	/**
	 * 请求时间戳
	 */
	private Long timestamp;
	/**
	 * 请求地址
	 */
	private String requestUrl;
	/**
	 * 报文大小
	 */
	private Long bytesSent;
	/**
	 * ref
	 */
	private String referer;
	/**
	 * user agent
	 */
	private String userAgent;
}
