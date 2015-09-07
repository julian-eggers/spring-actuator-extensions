package io.spring.boot.actuate.health;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;
import org.springframework.util.Assert;

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
				int currentSize = getCurrentSize(queueCheck);
				int maxSize = queueCheck.getMaxSize();
				int currentConsumers = getCurrentConsumers(queueCheck);
				int minConsumers = queueCheck.getMinConsumers();

				Map<String, Object> details = new LinkedHashMap<String, Object>();
				details.put("status", Status.UP.getCode());
				details.put("currentSize", Integer.valueOf(currentSize));
				details.put("maxSize", Integer.valueOf(maxSize));
				details.put("currentConsumers", Integer.valueOf(currentConsumers));
				details.put("minConsumers", Integer.valueOf(minConsumers));
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

	private int getCurrentSize(QueueCheck queueCheck)
	{
		RabbitAdmin rabbitAdmin = queueCheck.getRabbitAdmin();
		String queueName = queueCheck.getQueue().getName();
		Properties props = rabbitAdmin.getQueueProperties(queueName);
		return Integer.parseInt(props.get("QUEUE_MESSAGE_COUNT").toString());
	}

	private int getCurrentConsumers(QueueCheck queueCheck)
	{
		RabbitAdmin rabbitAdmin = queueCheck.getRabbitAdmin();
		String queueName = queueCheck.getQueue().getName();
		Properties props = rabbitAdmin.getQueueProperties(queueName);
		return Integer.parseInt(props.get("QUEUE_CONSUMER_COUNT").toString());
	}

	public void addQueueCheck(Queue queue, int maxSize)
	{
		queueChecks.add(new QueueCheck(queue, maxSize, 1));
	}

	public void addQueueCheck(Queue queue, int maxSize, int minConsumers)
	{
		queueChecks.add(new QueueCheck(queue, maxSize, minConsumers));
	}

	private static class QueueCheck
	{
		private Queue queue;
		private RabbitAdmin rabbitAdmin;
		private int maxSize;
		private int minConsumers;

		public QueueCheck(Queue queue, int maxSize, int minConsumers)
		{
			this.queue = queue;
			this.maxSize = maxSize;
			this.minConsumers = minConsumers;
			validateRabbitAdmin(queue);
		}
		
		private void validateRabbitAdmin(Queue queue)
		{
			Assert.notEmpty(queue.getDeclaringAdmins(), "At least one RabbitAdmin must be declared");
			Object object = queue.getDeclaringAdmins().iterator().next();
			Assert.isInstanceOf(RabbitAdmin.class, object, "DeclaringAdmin must be a RabbitAdmin");
			this.rabbitAdmin = (RabbitAdmin) object;
		}

		public Queue getQueue()
		{
			return queue;
		}

		public RabbitAdmin getRabbitAdmin()
		{
			return rabbitAdmin;
		}

		public int getMaxSize()
		{
			return maxSize;
		}

		public int getMinConsumers()
		{
			return minConsumers;
		}
	}
}