spring-actuator-extensions
============

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.itelg.spring/spring-actuator-extensions/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.itelg.spring/spring-actuator-extensions)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/3b7175029c144f91aa297355d4223158)](https://www.codacy.com/app/eggers-julian/spring-actuator-extensions)
[![Coverage Status](https://coveralls.io/repos/julian-eggers/spring-actuator-extensions/badge.svg?branch=master&service=github)](https://coveralls.io/github/julian-eggers/spring-actuator-extensions?branch=master)
[![Build Status](https://travis-ci.org/julian-eggers/spring-actuator-extensions.svg?branch=master)](https://travis-ci.org/julian-eggers/spring-actuator-extensions)

New HealthIndicators for Spring-Boot

#### Maven
```xml
<dependencies>
	<dependency>
		<groupId>com.itelg.spring</groupId>
		<artifactId>spring-actuator-extensions</artifactId>
		<version>0.2.4-RELEASE</version>
	</dependency>
</dependencies>
```

#### Example
```java
@Bean
public HealthIndicator queueCheckHealthIndicator()
{
	RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
	healthIndicator.addQueueCheck(exampleQueue1, 10000, 1);
	healthIndicator.addQueueCheck(exampleQueue2, 50000, 3);
	return healthIndicator;
}
```

#### Response ([health.json](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-health))
```json
{
	"status" : "DOWN",
	"queueCheck" : 
	{
		"status" : "DOWN",
		"com.examle.exampleQueue1" : 
		{
			"status" : "UP",
			"currentMessageCount" : 214,
			"maxMessageCount" : 10000,
			"currentConsumerCount" : 5,
			"minConsumerCount" : 1
		},
		"com.example.exampleQueue2" : 
		{
			"status" : "DOWN",
			"currentMessageCount" : 67377,
			"maxMessageCount" : 50000,
			"currentConsumerCount" : 0,
			"minConsumerCount" : 3
		}
	}
}
```
