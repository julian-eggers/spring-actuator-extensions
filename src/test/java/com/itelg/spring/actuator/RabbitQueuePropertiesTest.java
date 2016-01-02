package com.itelg.spring.actuator;

import org.junit.Assert;
import org.junit.Test;

public class RabbitQueuePropertiesTest
{
	@Test
	public void testToString()
	{
		Assert.assertTrue(new RabbitQueueProperties().toString().startsWith("RabbitQueueProperties"));
	}
}