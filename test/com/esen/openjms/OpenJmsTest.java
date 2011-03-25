package com.esen.openjms;

import static org.junit.Assert.assertEquals;

import javax.jms.JMSException;

import org.exolab.jms.administration.JmsAdminServerIfc;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 测试openjms消息发送与接收
 * @author sam
 *
 */
public class OpenJmsTest {
	private static JmsController jms;

	@BeforeClass
	public static void init() {
		jms = new JmsController();
		jms.start();
	}

	@Test
	public void testSend() {
		String msg = "hi";
		String sendto = "queue3";
		jms.send(sendto, msg);
		String m = jms.receive(sendto);
		Assert.assertEquals(msg, m);
	}

	@Test
	public void testAdmin() throws JMSException {
		String dest = "test1";
		JmsAdminServerIfc admin = jms.getAdmin();
		int destcount = admin.getAllDestinations().size();
		admin.addDestination(dest, true);
		assertEquals(destcount + 1, admin.getAllDestinations().size());
		admin.removeDestination(dest);
	}
}
