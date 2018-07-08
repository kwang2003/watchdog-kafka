package com.pachiraframework.watchdog.kafka.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.pachiraframework.watchdog.kafka.consumer.data.FileBeatLogMessage;

import io.krakens.grok.api.Match;

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
@Component
public class NginxAccessLogConsumer extends AbstractKafkaConsumer {
	private static final String PATTERN = "%{NGINXACCESS}";
	@KafkaListener(topics = "nginx-access-log-topic")
	public void listen(ConsumerRecord<Integer, String> cr) throws Exception {
		FileBeatLogMessage fileBeatLogMessage = consumeFileBeatLogMessage(cr);
		Match match = getGrok().match(fileBeatLogMessage.getMessage());
		final Map<String, Object> capture = match.capture();
	}

	@Override
	protected String matchPattern() {
		return PATTERN;
	}
}
