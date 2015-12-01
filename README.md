spring-actuator-extensions
============

[![Build Status](https://travis-ci.org/julian-eggers/spring-actuator-extensions.svg?branch=master)](https://travis-ci.org/julian-eggers/spring-actuator-extensions)
[![Coverage Status](https://coveralls.io/repos/julian-eggers/spring-actuator-extensions/badge.svg?branch=master&service=github)](https://coveralls.io/github/julian-eggers/spring-actuator-extensions?branch=master)

#### Maven
```xml
<dependencies>
	<dependency>
		<groupId>com.itelg.spring</groupId>
		<artifactId>spring-actuator-extensions</artifactId>
		<version>0.2.2-RELEASE</version>
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

#### Response (health.json)
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
  },
```
