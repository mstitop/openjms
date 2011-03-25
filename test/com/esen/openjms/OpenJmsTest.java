package com.esen.openjms;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试openjms消息发送与接收
 * @author sam
 *
 */
public class OpenJmsTest {
	@Test
	public void testSend() {
		String msg = "hi";
		String sendto = "queue3";
		JmsController jms = new JmsController();
		jms.start();
		jms.send(sendto, msg);
		String m = jms.receive(sendto);
		Assert.assertEquals(msg, m);
	}
}
