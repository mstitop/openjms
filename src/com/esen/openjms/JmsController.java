package com.esen.openjms;

import java.net.MalformedURLException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;
import org.exolab.jms.server.JmsServer;

public class JmsController {
	public static final String url = "tcp://localhost:3035";

	JmsServer server;

	public JmsAdminServerIfc getAdmin() {
		JmsAdminServerIfc admin = null;
		try {
			admin = AdminConnectionFactory.create(url);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (JMSException e) {
			e.printStackTrace();
		}
		return admin;
	}

	public static void main(String[] args) {
		final JmsController controller = new JmsController();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				controller.start();
			}
		});
		thread.setDaemon(true);
		thread.start();
		controller.send(new String[] { "queue2" });
		controller.stop(true);
	}

	public void stop(boolean exit) {
		if (exit) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ignore) {
			}
			System.exit(0);
		}
	}

	public String receive(String sendto) {
		String msg = "";
		Context context = null;
		ConnectionFactory factory = null;
		Connection connection = null;
		String factoryName = "ConnectionFactory";
		String destName = null;
		Destination dest = null;
		int count = 1;
		Session session = null;
		MessageConsumer receiver = null;

		destName = sendto;

		try {
			// create the JNDI initial context
			context = new InitialContext();

			// look up the ConnectionFactory
			factory = (ConnectionFactory) context.lookup(factoryName);

			// look up the Destination
			dest = (Destination) context.lookup(destName);

			// create the connection
			connection = factory.createConnection();

			// create the session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// create the receiver
			receiver = session.createConsumer(dest);

			// start the connection, to enable message receipt
			connection.start();

			for (int i = 0; i < count; ++i) {
				Message message = receiver.receive();
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					msg = text.getText();
					System.out.println("Received: " + text.getText());
				}
				else if (message != null) {
					System.out.println("Received non text message");
				}
			}
		}
		catch (JMSException exception) {
			exception.printStackTrace();
		}
		catch (NamingException exception) {
			exception.printStackTrace();
		}
		finally {
			// close the context
			if (context != null) {
				try {
					context.close();
				}
				catch (NamingException exception) {
					exception.printStackTrace();
				}
			}

			// close the connection
			if (connection != null) {
				try {
					connection.close();
				}
				catch (JMSException exception) {
					exception.printStackTrace();
				}
			}
		}
		return msg;
	}

	public void send(String sendto, String msg) {
		Context context = null;
		ConnectionFactory factory = null;
		Connection connection = null;
		String factoryName = "ConnectionFactory";
		String destName = null;
		Destination dest = null;
		int count = 1;
		Session session = null;
		MessageProducer sender = null;
		String text = msg;

		destName = sendto;

		try {
			context = new InitialContext();
			factory = (ConnectionFactory) context.lookup(factoryName);
			dest = (Destination) context.lookup(destName);
			connection = factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			sender = session.createProducer(dest);
			connection.start();
			for (int i = 0; i < count; ++i) {
				TextMessage message = session.createTextMessage();
				message.setText(text);
				sender.send(message);
				System.out.println("Sent: " + message.getText());
			}
		}
		catch (JMSException exception) {
			exception.printStackTrace();
		}
		catch (NamingException exception) {
			exception.printStackTrace();
		}
		finally {
			if (context != null) {
				try {
					context.close();
				}
				catch (NamingException exception) {
					exception.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				}
				catch (JMSException exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	public void send(String[] args) {
		Context context = null;
		ConnectionFactory factory = null;
		Connection connection = null;
		String factoryName = "ConnectionFactory";
		String destName = null;
		Destination dest = null;
		int count = 1;
		Session session = null;
		MessageProducer sender = null;
		String text = "Message ";

		if (args.length < 1 || args.length > 2) {
			System.out.println("usage: Sender <destination> [count]");
			System.exit(1);
		}

		destName = args[0];
		if (args.length == 2) {
			count = Integer.parseInt(args[1]);
		}

		try {
			context = new InitialContext();
			factory = (ConnectionFactory) context.lookup(factoryName);
			dest = (Destination) context.lookup(destName);
			connection = factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			sender = session.createProducer(dest);
			connection.start();
			for (int i = 0; i < count; ++i) {
				TextMessage message = session.createTextMessage();
				message.setText(text + (i + 1));
				sender.send(message);
				System.out.println("Sent: " + message.getText());
			}
		}
		catch (JMSException exception) {
			exception.printStackTrace();
		}
		catch (NamingException exception) {
			exception.printStackTrace();
		}
		finally {
			if (context != null) {
				try {
					context.close();
				}
				catch (NamingException exception) {
					exception.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				}
				catch (JMSException exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	public void start() {
		try {
			String p = getClass().getClassLoader().getResource("openjms.xml").getPath();
			server = new JmsServer(p);
			server.init();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
