package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.spring.boot.actuate.health.RabbitQueueCheckHealthIndicator;

@Configuration
public class HealthIndicatorConfiguration
{
	private @Autowired Queue test1Queue;
	private @Autowired Queue test2Queue;

	@Bean
	public HealthIndicator queueCheckHealthIndicator()
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		healthIndicator.addQueueCheck(test1Queue, 1000);
		healthIndicator.addQueueCheck(test2Queue, 50000, 3);
		return healthIndicator;
	}
}