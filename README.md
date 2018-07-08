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

### 示例日志数据（[web3.log](etc/web3.log)）
```
[main] 2018-07-04 16:39:58,659 INFO  [org.springframework.beans.factory.config.PropertyPlaceholderConfigurer.loadProperties(177)] Loading properties file from class path resource [properties/config.properties]
[main] 2018-07-04 16:39:58,736 INFO  [org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(577)] Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@30db95a1: defining beans [propertyConfigurer,XMemcacheClient,org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor,memcachedAddressProvider,memcachedClient,memcachedClientBuilder,memcachedClientInstance,org.springframework.beans.factory.config.MethodInvokingFactoryBean#0,com.google.code.ssm.Settings#0,cacheBase,readThroughSingleCache,readThroughMultiCache,readThroughAssignCache,updateSingleCache,updateMultiCache,updateAssignCache,invalidateSingleCache,invalidateMultiCache,invalidateAssignCache,incrementCounterInCache,decrementCounterInCache,readCounterFromCache,updateCounterInCache,org.springframework.aop.config.internalAutoProxyCreator,org.springframework.context.annotation.ConfigurationClassPostProcessor.importAwareProcessor]; root of factory hierarchy
[main] 2018-07-04 16:39:58,737 INFO  [org.springframework.beans.factory.support.DefaultListableBeanFactory.destroySingletons(434)] ++++++++++ singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@30db95a1: defining beans [propertyConfigurer,XMemcacheClient,org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.internalCommonAnnotationProcessor,memcachedAddressProvider,memcachedClient,memcachedClientBuilder,memcachedClientInstance,org.springframework.beans.factory.config.MethodInvokingFactoryBean#0,com.google.code.ssm.Settings#0,cacheBase,readThroughSingleCache,readThroughMultiCache,readThroughAssignCache,updateSingleCache,updateMultiCache,updateAssignCache,invalidateSingleCache,invalidateMultiCache,invalidateAssignCache,incrementCounterInCache,decrementCounterInCache,readCounterFromCache,updateCounterInCache,org.springframework.aop.config.internalAutoProxyCreator,org.springframework.context.annotation.ConfigurationClassPostProcessor.importAwareProcessor]; root of factory hierarchy
[main] 2018-07-04 16:39:58,738 ERROR [org.springframework.web.context.ContextLoader.initWebApplicationContext(307)] Context initialization failed
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'XMemcacheClient' defined in URL [jar:file:/C:/Users/Administrator/.m2/repository/com/d/b/a-core/3.0.4/a-core-3.0.4.jar!/com/a/b/core/cache/memcache/XMemcacheClient.class]: BeanPostProcessor before instantiation of bean failed; nested exception is java.lang.NoClassDefFoundError: org/aspectj/lang/annotation/Aspect
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:452)
	at org.springframework.beans.factory.support.AbstractBeanFactory$1.getObject(AbstractBeanFactory.java:294)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:225)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:291)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:193)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:605)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:925)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:472)
	at org.springframework.web.context.ContextLoader.configureAndRefreshWebApplicationContext(ContextLoader.java:383)
	at org.springframework.web.context.ContextLoader.initWebApplicationContext(ContextLoader.java:283)
	at org.springframework.web.context.ContextLoaderListener.contextInitialized(ContextLoaderListener.java:111)
	at org.mortbay.jetty.handler.ContextHandler.startContext(ContextHandler.java:549)
	at org.mortbay.jetty.servlet.Context.startContext(Context.java:136)
	at org.mortbay.jetty.webapp.WebAppContext.startContext(WebAppContext.java:1282)
	at org.mortbay.jetty.handler.ContextHandler.doStart(ContextHandler.java:518)
	at org.mortbay.jetty.webapp.WebAppContext.doStart(WebAppContext.java:499)
	at org.mortbay.component.AbstractLifeCycle.start(AbstractLifeCycle.java:50)
	at org.mortbay.jetty.handler.HandlerWrapper.doStart(HandlerWrapper.java:130)
	at org.mortbay.jetty.Server.doStart(Server.java:224)
	at org.mortbay.component.AbstractLifeCycle.start(AbstractLifeCycle.java:50)
	at runjettyrun.Bootstrap.main(Bootstrap.java:97)
Caused by: java.lang.NoClassDefFoundError: org/aspectj/lang/annotation/Aspect
	at org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory.hasAspectAnnotation(AbstractAspectJAdvisorFactory.java:119)
	at org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory.isAspect(AbstractAspectJAdvisorFactory.java:115)
	at org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator.isInfrastructureClass(AnnotationAwareAspectJAutoProxyCreator.java:100)
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.postProcessBeforeInstantiation(AbstractAutoProxyCreator.java:278)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInstantiation(AbstractAutowireCapableBeanFactory.java:880)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.resolveBeforeInstantiation(AbstractAutowireCapableBeanFactory.java:852)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:446)
	... 20 more
Caused by: java.lang.ClassNotFoundException: org.aspectj.lang.annotation.Aspect
	at java.net.URLClassLoader$1.run(URLClassLoader.java:202)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:190)
	at org.mortbay.jetty.webapp.WebAppClassLoader.loadClass(WebAppClassLoader.java:392)
	at runjettyrun.ProjectClassLoader.loadClass(ProjectClassLoader.java:89)
	... 27 more
[main] 2018-07-04 16:40:58,738 ERROR [org.springframework.web.context.ContextLoader.initWebApplicationContext(307)] === initialization failed

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

### kafka服务器端相关操作命令
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

创建异常日志队列5个分区
> kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic exception-topic