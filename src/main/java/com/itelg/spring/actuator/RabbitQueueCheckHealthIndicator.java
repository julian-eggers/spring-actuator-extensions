package com.itelg.spring.actuator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

public class RabbitQueueCheckHealthIndicator extends AbstractHealthIndicator
{
	private static final Logger log = LoggerFactory.getLogger(RabbitQueueCheckHealthIndicator.class);
	private List<QueueCheck> queueChecks = new ArrayList<QueueCheck>();

	@Override
	protected void doHealthCheck(Builder builder) throws Exception
	{
		builder.up();
		
		for (QueueCheck queueCheck : queueChecks)
		{
			try
			{
				String queueName = queueCheck.getQueue().getName();
				int currentSize = RabbitQueuePropertiesUtil.getMessageCount(queueCheck.getQueue());
				int maxSize = queueCheck.getMaxSize();
				int currentConsumers = RabbitQueuePropertiesUtil.getConsumerCount(queueCheck.getQueue());
				int minConsumers = queueCheck.getMinConsumers();

				Map<String, Object> details = new LinkedHashMap<String, Object>();
				details.put("status", Status.UP.getCode());
				details.put("currentMessageCount", Integer.valueOf(currentSize));
				details.put("maxMessageCount", Integer.valueOf(maxSize));
				details.put("currentConsumerCount", Integer.valueOf(currentConsumers));
				details.put("minConsumerCount", Integer.valueOf(minConsumers));
				builder.withDetail(queueName, details);

				if (currentSize > maxSize)
				{
					builder.down();
					details.put("status", Status.DOWN.getCode());
					log.warn(queueName + ": Too many messages ready (Current: " + currentSize + ", "
					        + "Max-Messages: " + queueCheck.getMaxSize() + ")");
				}

				if (currentConsumers < minConsumers)
				{
					builder.down();
					details.put("status", Status.DOWN.getCode());
					log.warn(queueName + ": Not enough consumers active (Current: " + currentConsumers + ", "
					        + "Min-Consumers: " + queueCheck.getMinConsumers() + ")");
				}
			}
			catch (Exception e)
			{
				log.error(e.getMessage(), e);
			}
		}
	}

	public void addQueueCheck(Queue queue, int maxSize)
	{
		queueChecks.add(new QueueCheck(queue, maxSize, 1));
	}

	public void addQueueCheck(Queue queue, int maxSize, int minConsumers)
	{
		queueChecks.add(new QueueCheck(queue, maxSize, minConsumers));
	}
}