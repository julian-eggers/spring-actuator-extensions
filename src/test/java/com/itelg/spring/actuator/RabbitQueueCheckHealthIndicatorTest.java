package com.itelg.spring.actuator;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

@RunWith(PowerMockRunner.class)
@PrepareForTest( RabbitQueuePropertiesUtil.class )
public class RabbitQueueCheckHealthIndicatorTest
{
	@Test
	public void testDoHealthCheck_noQueueChecks() throws Exception
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		
		Builder builder = new Builder(Status.OUT_OF_SERVICE);
		healthIndicator.doHealthCheck(builder);
		Assert.assertEquals(Status.UP, builder.build().getStatus());
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testDoHealthCheck_singleQueueCheckUp() throws Exception
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		Queue queue = generateQueue("test");
		healthIndicator.addQueueCheck(queue, 10000, 2);
		
		PowerMockito.mockStatic(RabbitQueuePropertiesUtil.class);
		PowerMockito.when(RabbitQueuePropertiesUtil.getMessageCount(queue)).thenReturn(Integer.valueOf(5883));
		PowerMockito.when(RabbitQueuePropertiesUtil.getConsumerCount(queue)).thenReturn(Integer.valueOf(4));
		
		Builder builder = new Builder(Status.OUT_OF_SERVICE);
		healthIndicator.doHealthCheck(builder);
		Health health = builder.build();
		Assert.assertEquals(Status.UP, health.getStatus());
		Assert.assertNotNull(health.getDetails().get("test"));
		Map<String, Object> details = (Map<String, Object>) health.getDetails().get("test");
		Assert.assertEquals(Status.UP.getCode(), details.get("status"));
		Assert.assertEquals(5883, details.get("currentMessageCount"));
		Assert.assertEquals(10000, details.get("maxMessageCount"));
		Assert.assertEquals(4, details.get("currentConsumerCount"));
		Assert.assertEquals(2, details.get("minConsumerCount"));
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testDoHealthCheck_singleQueueCheck_queueSizeDown() throws Exception
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		Queue queue = generateQueue("test");
		healthIndicator.addQueueCheck(queue, 10000, 2);
		
		PowerMockito.mockStatic(RabbitQueuePropertiesUtil.class);
		PowerMockito.when(RabbitQueuePropertiesUtil.getMessageCount(queue)).thenReturn(Integer.valueOf(15883));
		PowerMockito.when(RabbitQueuePropertiesUtil.getConsumerCount(queue)).thenReturn(Integer.valueOf(4));
		
		Builder builder = new Builder(Status.OUT_OF_SERVICE);
		healthIndicator.doHealthCheck(builder);
		Health health = builder.build();
		Assert.assertEquals(Status.DOWN, health.getStatus());
		Assert.assertNotNull(health.getDetails().get("test"));
		Map<String, Object> details = (Map<String, Object>) health.getDetails().get("test");
		Assert.assertEquals(Status.DOWN.getCode(), details.get("status"));
		Assert.assertEquals(15883, details.get("currentMessageCount"));
		Assert.assertEquals(10000, details.get("maxMessageCount"));
		Assert.assertEquals(4, details.get("currentConsumerCount"));
		Assert.assertEquals(2, details.get("minConsumerCount"));
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testDoHealthCheck_singleQueueCheck_consumerDown() throws Exception
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		Queue queue = generateQueue("test");
		healthIndicator.addQueueCheck(queue, 10000, 2);
		
		PowerMockito.mockStatic(RabbitQueuePropertiesUtil.class);
		PowerMockito.when(RabbitQueuePropertiesUtil.getMessageCount(queue)).thenReturn(Integer.valueOf(5883));
		PowerMockito.when(RabbitQueuePropertiesUtil.getConsumerCount(queue)).thenReturn(Integer.valueOf(1));
		
		Builder builder = new Builder(Status.OUT_OF_SERVICE);
		healthIndicator.doHealthCheck(builder);
		Health health = builder.build();
		Assert.assertEquals(Status.DOWN, health.getStatus());
		Assert.assertNotNull(health.getDetails().get("test"));
		Map<String, Object> details = (Map<String, Object>) health.getDetails().get("test");
		Assert.assertEquals(Status.DOWN.getCode(), details.get("status"));
		Assert.assertEquals(5883, details.get("currentMessageCount"));
		Assert.assertEquals(10000, details.get("maxMessageCount"));
		Assert.assertEquals(1, details.get("currentConsumerCount"));
		Assert.assertEquals(2, details.get("minConsumerCount"));
	}
	
	@SuppressWarnings({ "boxing" })
	@Test
	public void testDoHealthCheck_singleQueueCheck_MetricException() throws Exception
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		Queue queue = generateQueue("test");
		healthIndicator.addQueueCheck(queue, 10000, 2);
		
		PowerMockito.mockStatic(RabbitQueuePropertiesUtil.class);
		PowerMockito.when(RabbitQueuePropertiesUtil.getMessageCount(queue)).thenReturn(Integer.valueOf(5883));
		PowerMockito.when(RabbitQueuePropertiesUtil.getConsumerCount(queue)).thenThrow(new RuntimeException());
		
		Builder builder = new Builder(Status.OUT_OF_SERVICE);
		healthIndicator.doHealthCheck(builder);
		Health health = builder.build();
		Assert.assertEquals(Status.DOWN, health.getStatus());
		Assert.assertNull(health.getDetails().get("test"));
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testDoHealthCheck_multipleQueueChecks_oneUpOneDown() throws Exception
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		Queue queue1 = generateQueue("test1");
		healthIndicator.addQueueCheck(queue1, 10000, 2);
		Queue queue2 = generateQueue("test2");
		healthIndicator.addQueueCheck(queue2, 40000, 5);
		
		PowerMockito.mockStatic(RabbitQueuePropertiesUtil.class);
		PowerMockito.when(RabbitQueuePropertiesUtil.getMessageCount(queue1)).thenReturn(Integer.valueOf(15883));
		PowerMockito.when(RabbitQueuePropertiesUtil.getConsumerCount(queue1)).thenReturn(Integer.valueOf(1));
		PowerMockito.when(RabbitQueuePropertiesUtil.getMessageCount(queue2)).thenReturn(Integer.valueOf(5883));
		PowerMockito.when(RabbitQueuePropertiesUtil.getConsumerCount(queue2)).thenReturn(Integer.valueOf(10));
		
		Builder builder = new Builder(Status.OUT_OF_SERVICE);
		healthIndicator.doHealthCheck(builder);
		Health health = builder.build();
		Assert.assertEquals(Status.DOWN, health.getStatus());
		Assert.assertEquals(2, health.getDetails().size());
		
		Assert.assertNotNull(health.getDetails().get("test1"));
		Map<String, Object> details1 = (Map<String, Object>) health.getDetails().get("test1");
		Assert.assertEquals(Status.DOWN.getCode(), details1.get("status"));
		Assert.assertEquals(15883, details1.get("currentMessageCount"));
		Assert.assertEquals(10000, details1.get("maxMessageCount"));
		Assert.assertEquals(1, details1.get("currentConsumerCount"));
		Assert.assertEquals(2, details1.get("minConsumerCount"));
		
		Assert.assertNotNull(health.getDetails().get("test2"));
		Map<String, Object> details2 = (Map<String, Object>) health.getDetails().get("test2");
		Assert.assertEquals(Status.UP.getCode(), details2.get("status"));
		Assert.assertEquals(5883, details2.get("currentMessageCount"));
		Assert.assertEquals(40000, details2.get("maxMessageCount"));
		Assert.assertEquals(10, details2.get("currentConsumerCount"));
		Assert.assertEquals(5, details2.get("minConsumerCount"));
	}
	
	@Test
	public void testAddQueueCheck_maxMessageCountAndMinConsumerCount()
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		healthIndicator.addQueueCheck(generateQueue("test"), 10000, 5);
		healthIndicator.addQueueCheck(generateQueue("test"), 5000, 2);
		Assert.assertEquals(2, healthIndicator.getQueueChecks().size());
	}
	
	@Test
	public void testAddQueueCheck_maxMessageCount()
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		healthIndicator.addQueueCheck(generateQueue("test"), 10000);
		healthIndicator.addQueueCheck(generateQueue("test"), 5000);
		Assert.assertEquals(2, healthIndicator.getQueueChecks().size());
	}
	
	@Test
	public void testGetQueueChecks()
	{
		RabbitQueueCheckHealthIndicator healthIndicator = new RabbitQueueCheckHealthIndicator();
		Assert.assertNotNull(healthIndicator.getQueueChecks());
		
		Queue queue = generateQueue("test");
		healthIndicator.addQueueCheck(queue, 1);
		healthIndicator.addQueueCheck(queue, 1);
		Assert.assertEquals(2, healthIndicator.getQueueChecks().size());
	}
	
	private Queue generateQueue(String name)
	{
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		
		Queue queue = new Queue(name);
		queue.setAdminsThatShouldDeclare(rabbitAdmin);
		
		return queue;
	}
}