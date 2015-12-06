package com.itelg.spring.actuator;

import java.util.Properties;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.util.Assert;

public class RabbitQueuePropertiesUtil
{
	private RabbitQueuePropertiesUtil()
	{

	}

	public static int getMessageCount(Queue queue)
	{
		Assert.notNull(queue, "'queue' cannot be null");
		RabbitAdmin rabbitAdmin = validateRabbitAdmin(queue);
		Object messageCount = getProperty(rabbitAdmin, queue, "QUEUE_MESSAGE_COUNT");
		Assert.notNull(messageCount);
		Assert.isInstanceOf(Integer.class, messageCount);
		return ((Integer) messageCount).intValue();
	}

	public static int getConsumerCount(Queue queue)
	{
		Assert.notNull(queue, "'queue' cannot be null");
		RabbitAdmin rabbitAdmin = validateRabbitAdmin(queue);
		Object consumerCount = getProperty(rabbitAdmin, queue, "QUEUE_CONSUMER_COUNT");
		Assert.notNull(consumerCount);
		Assert.isInstanceOf(Integer.class, consumerCount);
		return ((Integer) consumerCount).intValue();
	}

	private static RabbitAdmin validateRabbitAdmin(Queue queue)
	{
		Assert.notEmpty(queue.getDeclaringAdmins(), "At least one RabbitAdmin must be declared");
		Object object = queue.getDeclaringAdmins().iterator().next();
		Assert.isInstanceOf(RabbitAdmin.class, object, "DeclaringAdmin must be a RabbitAdmin");
		return (RabbitAdmin) object;
	}

	private static Object getProperty(RabbitAdmin rabbitAdmin, Queue queue, String key)
	{
		Properties properties = rabbitAdmin.getQueueProperties(queue.getName());
		Assert.isTrue(properties.containsKey(key));
		return properties.get(key);
	}
}