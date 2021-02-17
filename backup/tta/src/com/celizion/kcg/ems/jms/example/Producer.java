package com.celizion.kcg.ems.jms.example;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
	private final static String QUEUE_NAME = "hello";

	public static void main(String[] args) {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setPort(5672);
		factory.setUsername("goodsw");
		factory.setPassword("good1018");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			for (int i = 0; i <= 100; i++) {
				channel.queueDeclare(QUEUE_NAME, false, false, false, null);
				String message = "[" + i + "] Hello World!" + (int) (Math.random() * 100);
				channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
				System.out.println(" [x] Set '" + message + "'");
				Thread.sleep(10);
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
