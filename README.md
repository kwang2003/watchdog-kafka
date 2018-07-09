### 1.系统环境
- JDK1.8
- SpringBoot 2.0.3.RELEASE
- kafka_2.12-1.1.0 
- filebeat-6.3.0-windows-x86_64
- grok日志解析
- Maven
- lombok
- guava
- gson


### 2.filebeat配置文件(一个filebeat客户端收集多种不同文件的日志，并将不同日志输出到不同的kafka消息队列中)
[filebeat.yml](etc/filebeat.yml)
核心配置-输出到kafka：

```
#-------------------------- Kafka output --------------------------------------
output.kafka:
  hosts: ["localhost:9092"]
  topic: '%{[fields][topics]}'
  required_acks: 1
```

核心配置-监听指定文件--监听多文件示例，将不同的文件输出到不同的topic中，并且实现识别多行内容解析识别

```
#=========================== Filebeat inputs =============================

filebeat.inputs:
- type: log
  enabled: true
  paths:
    - D:\home\admin\output\logs\javalog\nginx-access.log
  encoding:
    utf-8
  fields:
    ip: 10.130.53.189
    domain: abc.com
    topics: nginx-access-log-topic
    
- type: log
  enabled: true
  paths:
    - D:\home\admin\output\logs\javalog\shoppingmall_search_web.log
  encoding:
    utf-8
  fields:
    ip: 10.130.53.189
    app_id: order-service
    topics: application-log-topic
  multiline.pattern: ^\[
  multiline.negate: true
  multiline.match: after
```

filebeat的读取日志文件位置保存在filebeat-6.3.0-windows-x86_64\data目录下

- registry文件内容如下(核心)

```
[{"source":"D:\\home\\admin\\output\\logs\\javalog\\nginx-access.log","offset":85717,"timestamp":"2018-07-09T17:17:58.8132721+08:00","ttl":-1,"type":"log","FileStateOS":{"idxhi":1572864,"idxlo":79432,"vol":877113}},{"source":"D:\\home\\admin\\output\\logs\\javalog\\shoppingmall_search_web.log","offset":6993,"timestamp":"2018-07-09T17:17:58.7192668+08:00","ttl":-1,"type":"log","FileStateOS":{"idxhi":1900544,"idxlo":797606,"vol":877113}}]
```

- meta.json文件内容

```
{"uuid":"2bfaae15-f4ee-4f20-8743-9aae63428f95"}

```
### 3.日志监控模块
监控系统-kafka消息监听模块。通过filebeat从服务器上收集日志信息，传递到kafka，在该模块中实现消息的监听，对日志进行解析，进而做下一步的处理操作.使用grok作为日志分析框架提炼日志内容

#### 3.1收集并解析log4j日志
##### 3.1.1 log4j日志输出文件格式样例：[log4j.xml](etc/log4j.xml)
```xml
<appender name="file-log" class="org.apache.log4j.DailyRollingFileAppender">
	<param name="file" value="/home/admin/output/logs/javalog/web3.log" />
	<param name="append" value="true" />
	<param name="DatePattern" value="'.'yyyy-MM-dd" />
	<layout class="org.apache.log4j.PatternLayout">
		<param name="ConversionPattern" value="[%t] %d %-5p [%c.%M(%L)] %m%n" />
	</layout>
</appender>
```

##### 3.1.2示例日志数据（[web3.log](etc/web3.log)）
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

###### 3.1.3kafka中订阅的json格式消息格式

```json
{"@timestamp":"2018-07-07T02:46:46.780Z","@metadata":{"beat":"filebeat","type":"doc","version":"6.3.0","topic":"application-log-topic"},"prospector":{"type":"log"},"input":{"type":"log"},"beat":{"name":"01AD58697812703","hostname":"01AD58697812703","version":"6.3.0"},"host":{"name":"01AD58697812703"},"source":"D:\\home\\admin\\output\\logs\\javalog\\order-service.log","offset":1366406,"message":"[main] 2018-07-07 10:46:41,641 INFO  [org.springframework.context.support.ClassPathXmlApplicationContext.prepareRefresh(503)] Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@7e9a5fbe: startup date [Sat Jul 07 10:46:41 CST 2018]; root of context hierarchy"}
```

#### 3.2收集并解析nginx access日志
##### 3.2.1 nginx日志输出文件格式样例

```
log_format demo_access "$remote_addr - $remote_user [$time_local] " 
                            '"$request" $status $body_bytes_sent '
                            '"$http_referer" "$http_user_agent" $YZHDID';
```

##### 3.1.2 nginx访问日志输出样例[nginx-access.log](etc/nginx-access.log)

```
106.11.155.221 - - [08/Jul/2018:17:04:39 +0800] "GET /_bvzjbozkbnckbmXbjX_40_chX_RO_bmXbjX_41_chX-se.html?bid=137&p=2&sp=1 HTTP/1.1" 200 12881 "-" "YisouSpider" -
203.208.60.216 - - [08/Jul/2018:17:08:17 +0800] "GET /js/jquery-1.11.2.min.js HTTP/1.1" 302 0 "http://www.abc.com/_bujyboyebeyybfmc-se.html?cid=815&type=1&spot=0&bid=55&sp=0" "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Safari/537.36" -
101.199.108.119 - - [08/Jul/2018:17:10:58 +0800] "GET /css/sobox.css HTTP/1.1" 200 1592 "http://www.abc.com/F70ES1000-se.html" "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36" -
101.199.108.119 - - [08/Jul/2018:17:10:58 +0800] "GET /js/jquery-1.11.2.min.js HTTP/1.1" 302 0 "http://www.abc.com/F70ES1000-se.html" "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36" -
```

##### 3.1.3 kafka中订阅到的nginx访问日志json格式样例

```
{"@timestamp":"2018-07-09T09:12:53.691Z","@metadata":{"beat":"filebeat","type":"doc","version":"6.3.0","topic":"nginx-access-log-topic"},"beat":{"name":"01AD58697812703","hostname":"01AD58697812703","version":"6.3.0"},"host":{"name":"01AD58697812703"},"source":"D:\\home\\admin\\output\\logs\\javalog\\nginx-access.log","offset":196,"message":"127.0.0.1 - - [07/Jul/2018:23:00:11 +0800] \"GET /getName HTTP/1.1\" 200 23 \"-\" \"curl/7.19.7 (x86_64-redhat-linux-gnu) libcurl/7.19.7 NSS/3.16.2.3 Basic ECC zlib/1.2.3 libidn/1.18 libssh2/1.4.2\" -","input":{"type":"log"},"fields":{"domain":"abc.com","topics":"nginx-access-log-topic","ip":"10.130.53.189"},"prospector":{"type":"log"}}
```

##### 3.1.4 试用grok的解析pattern

```
NGINX_ACCESSLOG_COMBINED_01 %{IPORHOST:clientip} - %{USER:ident} \[%{HTTPDATE:timestamp}\] \"%{NOTSPACE:request_method}%{SPACE}%{NOTSPACE:request_url}%{SPACE}%{NOTSPACE:http_version}\" %{INT:status} %{NUMBER:bytes_sent} \"%{DATA:http_referer}\" \"%{DATA:http_user_agent}\"
```

### kafka服务器端相关操作命令
启动kafka
> kafka-server-start.bat server.properties

启动kafka consumer(topic名称是application-log-topic)
> kafka-console-consumer.bat --zookeeper localhost:2181 --topic application-log-topic

查看topic列表
> kafka-topics.bat --list --zookeeper localhost:2181

启动topic为application-log-topic的生产者方（可以直接在控制台往topic中发送消息）
> kafka-console-producer.bat --broker-list localhost:9092 --topic application-log-topic

启动消费者，从topic的开始位置消费消息
> kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic application-log-topic --from-beginning

创建异常日志队列5个分区
> kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic exception-topic

