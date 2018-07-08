package com.pachiraframework.watchdog.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.InitializingBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pachiraframework.watchdog.kafka.consumer.data.FileBeatLogMessage;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import lombok.Getter;

/**
 * kafka 消费者端抽象，封装公共操作
 * @author wangxuzheng
 *
 */
public abstract class AbstractKafkaConsumer implements InitializingBean{
	private Gson gson = new GsonBuilder().create();
	@Getter
	protected Grok grok;
	
	/**
	 * 匹配的格式
	 * @return
	 */
	protected abstract String matchPattern();
	
	/**
	 * 把接收到的kafka中的filebeat消息解析成对象
	 * @param cr
	 * @return
	 */
	protected FileBeatLogMessage consumeFileBeatLogMessage(ConsumerRecord<Integer, String> cr) {
		String message = cr.value();
		FileBeatLogMessage fileBeatLogMessage = gson.fromJson(message, FileBeatLogMessage.class);
		return fileBeatLogMessage;
	}
	
	private GrokCompiler grokCompiler() {
		/* Create a new grokCompiler instance */
		GrokCompiler grokCompiler = GrokCompiler.newInstance();
		grokCompiler.registerDefaultPatterns();
		grokCompiler.registerPatternFromClasspath("/patterns/watchdog");
		return grokCompiler;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		GrokCompiler grokCompiler = grokCompiler();
		this.grok = grokCompiler.compile(this.matchPattern());
	}
	
}
