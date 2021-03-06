package com.itelg.spring.actuator;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.util.Assert;

public class QueueCheck
{
	private Queue queue;
	private RabbitAdmin rabbitAdmin;
	private int maxMessageCount;
	private int minConsumerCount;

	public QueueCheck(Queue queue, int maxMessageCount, int minConsumerCount)
	{
		this.queue = queue;
		this.maxMessageCount = maxMessageCount;
		this.minConsumerCount = minConsumerCount;
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

	public int getMaxMessageCount()
	{
		return maxMessageCount;
	}

	public int getMinConsumerCount()
	{
		return minConsumerCount;
	}
}