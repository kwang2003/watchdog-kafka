package com.pachiraframework.watchdog.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author wangxuzheng
 *
 */
@Component
public class AplicationLogTopicLogConsumer extends Log4jFormatLogConsumer {
	private static final String TOPIC_NAME = "application-log-topic";
	@KafkaListener(topics = TOPIC_NAME)
	public void listen(ConsumerRecord<Integer, String> cr) throws Exception {
		super.listen(cr);
	}
}
