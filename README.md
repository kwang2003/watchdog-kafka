### 系统环境
- JDK1.8
- SpringBoot 2.0.3.RELEASE
- kafka_2.12-1.1.0 
- filebeat-6.3.0-windows-x86_64
- grok日志解析
- Maven
- lombok
- guava
- gson

### 模块说明
监控系统-kafka消息监听模块。通过filebeat从服务器上收集日志信息，传递到kafka，在该模块中实现消息的监听，对日志进行解析，进而做下一步的处理操作.
使用grok作为日志分析框架提炼日志内容

### 日志格式：log4j.xml
```xml
<appender name="file-log" class="org.apache.log4j.DailyRollingFileAppender">
	<param name="file" value="/home/admin/output/logs/javalog/order-service.log" />
	<param name="append" value="true" />
	<param name="DatePattern" value="'.'yyyy-MM-dd" />
	<layout class="org.apache.log4j.PatternLayout">
		<param name="ConversionPattern" value="[%t] %d %-5p [%c.%M(%L)] %m%n" />
	</layout>
</appender>
```

### 示例日志数据
```
[main] 2018-07-07 10:31:12,138 INFO  [org.springframework.context.support.ClassPathXmlApplicationContext.prepareRefresh(503)] Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@7e9a5fbe: startup date [Sat Jul 07 10:31:12 CST 2018]; root of context hierarchy
[main] 2018-07-07 10:31:12,452 INFO  [org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions(315)] Loading XML bean definitions from class path resource [service-deploy.xml]
```

### kafka中订阅的消息格式

```json
{"@timestamp":"2018-07-07T02:46:46.780Z","@metadata":{"beat":"filebeat","type":"doc","version":"6.3.0","topic":"lxw1234"},"prospector":{"type":"log"},"input":{"type":"log"},"beat":{"name":"01AD58697812703","hostname":"01AD58697812703","version":"6.3.0"},"host":{"name":"01AD58697812703"},"source":"D:\\home\\admin\\output\\logs\\javalog\\order-service.log","offset":1366406,"message":"[main] 2018-07-07 10:46:41,641 INFO  [org.springframework.context.support.ClassPathXmlApplicationContext.prepareRefresh(503)] Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@7e9a5fbe: startup date [Sat Jul 07 10:46:41 CST 2018]; root of context hierarchy"}
```

### filebeat配置文件
[filebeat.yml](etc/filebeat.yml)
核心配置-输出到kafka：

```
#-------------------------- Kafka output --------------------------------------
output.kafka:
  hosts: ["localhost:9092"]
  topic: lxw1234
  required_acks: 1
```

核心配置-监听指定文件

```
#=========================== Filebeat inputs =============================

filebeat.inputs:

# Each - is an input. Most options can be set at the input level, so
# you can use different inputs for various configurations.
# Below are the input specific configurations.

- type: log

  # Change to true to enable this input configuration.
  enabled: true

  # Paths that should be crawled and fetched. Glob based paths.
  paths:
#    - /var/log/*.log
    - D:\web3.log
    #- c:\programdata\elasticsearch\logs\*
  encoding:
    utf-8
  # Exclude lines. A list of regular expressions to match. It drops the lines that are
  # matching any regular expression from the list.
  #exclude_lines: ['^DBG']

  # Include lines. A list of regular expressions to match. It exports the lines that are
  # matching any regular expression from the list.
  #include_lines: ['^ERR', '^WARN']

  # Exclude files. A list of regular expressions to match. Filebeat drops the files that
  # are matching any regular expression from the list. By default, no files are dropped.
  #exclude_files: ['.gz$']

  # Optional additional fields. These fields can be freely picked
  # to add additional information to the crawled log files for filtering
  fields:
    ip: 10.130.53.189
    app_id: order-service

  ### Multiline options

  # Mutiline can be used for log messages spanning multiple lines. This is common
  # for Java Stack Traces or C-Line Continuation

  # The regexp Pattern that has to be matched. The example pattern matches all lines starting with [
  multiline.pattern: ^\[

  # Defines if the pattern set under pattern should be negated or not. Default is false.
  multiline.negate: true

  # Match can be set to "after" or "before". It is used to define if lines should be append to a pattern
  # that was (not) matched before or after or as long as a pattern is not matched based on negate.
  # Note: After is the equivalent to previous and before is the equivalent to to next in Logstash
  multiline.match: after
```

### kafka服务器端相关操作命令你
启动kafka
> kafka-server-start.bat server.properties
启动kafka consumer(topic名称是lxw1234)
> kafka-console-consumer.bat --zookeeper localhost:2181 --topic lxw1234
查看topic列表
> kafka-topics.bat --list --zookeeper localhost:2181
启动topic为lxw1234的生产者方（可以直接在控制台往topic中发送消息）
> kafka-console-producer.bat --broker-list localhost:9092 --topic lxw1234
启动消费者，从topic的开始位置消费消息
> kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic lxw1234 --from-beginning