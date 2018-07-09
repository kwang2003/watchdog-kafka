package com.pachiraframework.watchdog.kafka.consumer;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import io.krakens.grok.api.Match;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NginxAccessLogConsumerTest {
	private NginxAccessLogConsumer consumer;
	@Before
	public void before() throws Exception {
		consumer = new NginxAccessLogConsumer() {
			@Override
			protected String matchPattern() {
				return "%{NGINX_ACCESSLOG_COMBINED_01}";
			}
			
		};
		consumer.afterPropertiesSet();
	}
	
	@Test
	public void testDateFormat() throws ParseException {
        
//		String format = "dd/MMM/YYYY:HH:mm:ss Z";
		String format = "dd/MMM/YYYY";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format,Locale.US);
//		String input = "08/Jul/2018:00:00:12 +0800";
		String input = "08/Jul/2018";
		Date date = dateFormat.parse(input);
		assertThat(date, notNullValue());
	}
	
	@Test
	public void testMatch() {
		String input = "127.0.0.1 - - [07/Jul/2018:23:00:11 +0800] \"GET /getName HTTP/1.1\" 200 23 \"-\" \"curl/7.19.7 (x86_64-redhat-linux-gnu) libcurl/7.19.7 NSS/3.16.2.3 Basic ECC zlib/1.2.3 libidn/1.18 libssh2/1.4.2\" -" ;
		Match match = consumer.getGrok().match(input);
		Map<String,Object> capture = match.capture();
		log.info("{}",capture);
	}
}
