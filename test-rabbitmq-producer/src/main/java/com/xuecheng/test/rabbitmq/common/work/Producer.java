package com.xuecheng.test.rabbitmq.common.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @Classname Producer01
 * @Description Work queues 工作模式 [生产者]
 * @Date 2020/2/5 17:20
 * @Created by 姜立成
 */
public class Producer {

    //队列名称
    private static final String QUEUE = "helloworld";

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = null;
        Channel channel = null;

        try {
            // 创建连接工厂
            ConnectionFactory connectionFactory = new ConnectionFactory();

            // 设置连接参数
            connectionFactory.setHost("192.168.142.128");
            connectionFactory.setPort(5672);
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("guest");
            connectionFactory.setVirtualHost("/");

            // 建立连接,创建通道
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            // 声明队列
            // queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
            channel.queueDeclare(QUEUE, true, false, false, null);

            String message = "Hello World!";

            // 发送消息
            // basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                channel.basicPublish("", QUEUE, null, message.concat(" --> " + i).getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("Send Message is:'" + message + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert channel != null;
            channel.close();
            connection.close();
        }

    }

}
