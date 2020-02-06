package com.xuecheng.test.rabbitmq.common.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @Classname Producer
 * @Description Publish/subscribe 工作模式 [生产者]
 * @Date 2020/2/5 21:11
 * @Created by 姜立成
 */
public class Producer {

    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

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

            // 声明交换机
            // exchangeDeclare(String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, Map<String, Object> arguments) throws IOException {
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT, true, false, null);

            // 声明队列
            // queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);

            // 绑定队列到交换机
            // queueBind(String queue, String exchange, String routingKey)
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_FANOUT_INFORM, "");
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_FANOUT_INFORM, "");

            String message = "Hello World!";

            // 发送消息
            // basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                channel.basicPublish(EXCHANGE_FANOUT_INFORM, "", null, message.concat(" --> " + i).getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("Send Message is:'" + message + "'");

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            assert channel != null;
            channel.close();
            connection.close();
        }

    }

}
