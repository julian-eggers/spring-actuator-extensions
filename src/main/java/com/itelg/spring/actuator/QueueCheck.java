package com.itelg.spring.actuator;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.util.Assert;

public class QueueCheck
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