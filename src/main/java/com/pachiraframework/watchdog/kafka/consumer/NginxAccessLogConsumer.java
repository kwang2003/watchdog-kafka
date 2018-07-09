package com.pachiraframework.watchdog.kafka.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.pachiraframework.watchdog.kafka.consumer.data.FileBeatLogMessage;
import com.pachiraframework.watchdog.kafka.consumer.data.NginxAccessLogMessage;

import io.krakens.grok.api.Match;
import lombok.extern.slf4j.Slf4j;

/**
 * nginx访问日志解析<br/>
 * <pre>
 *     log_format rrs_access "$remote_addr - $remote_user [$time_local] " 
                                '"$request" $status $body_bytes_sent '
                                '"$http_referer" "$http_user_agent" $YZHDID';
 * </pre>
 * @author wangxuzheng
 *
 */
@Slf4j
@Component
public class NginxAccessLogConsumer extends AbstractKafkaConsumer {
	private static final String PATTERN = "%{NGINX_ACCESSLOG_COMBINED_01}";
	// 日期格式  08/Jul/2018:00:00:12 +0800
	private static final String DATE_TIME_FORMAT_PATTERN_01 = "dd/MMM/YYYY:HH:mm:ss Z";
	@KafkaListener(topics = "nginx-access-log-topic")
	public void listen(ConsumerRecord<Integer, String> cr) throws Exception {
		FileBeatLogMessage fileBeatLogMessage = consumeFileBeatLogMessage(cr);
		Match match = getGrok().match(fileBeatLogMessage.getMessage());
		final Map<String, Object> capture = match.capture();
		NginxAccessLogMessage message = buildNginxAccessLogMessage(capture);
		message.setHost(fileBeatLogMessage.getFields()==null?fileBeatLogMessage.getBeat().getHostname():fileBeatLogMessage.getFields().get("ip"));
		message.setDomain(fileBeatLogMessage.getFields()== null?"":fileBeatLogMessage.getFields().get("domain"));
		log.info("{}",message);
	}
	
	private NginxAccessLogMessage buildNginxAccessLogMessage(Map<String, Object> capture) throws Exception{
		NginxAccessLogMessage message = new NginxAccessLogMessage();
		message.setBytesSent(Long.valueOf((String)capture.get("bytes_sent")));
		message.setUserAgent((String)capture.get("http_user_agent"));
		message.setUser((String)capture.get("ident"));
		message.setRequestUrl((String)capture.get("request_url"));
		message.setReferer((String)capture.get("http_referer"));
		message.setHttpVersion((String)capture.get("http_version"));
		message.setHttpMethod((String)capture.get("http_method"));
		String timestamp = (String)capture.get("timestamp");
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN_01,Locale.US);
		Date datetime = dateFormat.parse(timestamp);
		message.setTimestamp(datetime.getTime());
		return message;
	}

	@Override
	protected String matchPattern() {
		return PATTERN;
	}
}
