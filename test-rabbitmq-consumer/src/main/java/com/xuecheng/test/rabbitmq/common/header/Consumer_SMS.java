package com.xuecheng.test.rabbitmq.common.header;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Map;

/**
 * @Classname Consumer_SMS
 * @Description Header 工作模式 [信息消费者]
 * @Date 2020/2/5 21:23
 * @Created by 姜立成
 */
public class Consumer_SMS {

    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "header_queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "header_queue_inform_sms";
    private static final String EXCHANGE_HEADER_INFORM = "exchange_header_inform";

    public static void main(String[] args) {

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
            channel.exchangeDeclare(EXCHANGE_HEADER_INFORM, BuiltinExchangeType.HEADERS, true, false, null);

            // 声明队列
            // queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);

            Map<String, Object> headers_sms = new Hashtable<String, Object>();
            headers_sms.put("inform_type", "sms");

            // 绑定队列到交换机
            // queueBind(String queue, String exchange, String routingKey, Map<String, Object> arguments)
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_HEADER_INFORM, "", headers_sms);

            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    // super.handleDelivery(consumerTag, envelope, properties, body);
                    System.out.println("receive message: " + new String(body, StandardCharsets.UTF_8));
                }
            };

            channel.basicConsume(QUEUE_INFORM_SMS, true, consumer);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
