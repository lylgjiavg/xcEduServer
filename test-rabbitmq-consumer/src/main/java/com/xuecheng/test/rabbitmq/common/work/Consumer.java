package com.xuecheng.test.rabbitmq.common.work;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @Classname Consumer01
 * @Description Work queues 工作模式 [消费者]
 * @Date 2020/2/5 20:45
 * @Created by 姜立成
 */
public class Consumer {

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


            DefaultConsumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    // super.handleDelivery(consumerTag, envelope, properties, body);
                    System.out.println("receive message: " + new String(body, StandardCharsets.UTF_8));
                }
            };

            // 监听队列
            // basicConsume(String queue, boolean autoAck, Consumer callback)
            channel.basicConsume(QUEUE, true, consumer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
