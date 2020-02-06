package com.xuecheng.test.rabbitmq.springboot.handle;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.springboot.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Classname ReceiveHandler
 * @Description Rabbit 消费端
 * @Date 2020/2/6 14:51
 * @Created by 姜立成
 */
@Component
public class ReceiveHandler {

    //监听email队列
    @RabbitListener(queues = {RabbitConfig.QUEUE_INFORM_EMAIL})
    public void receive_email(String msg, Message message, Channel channel) {
        System.out.println("email client receive: " + msg);
    }

    //监听sms队列
    @RabbitListener(queues = {RabbitConfig.QUEUE_INFORM_SMS})
    public void receive_sms(String msg, Message message, Channel channel) {
        System.out.println("sms client receive: " + msg);
    }

}
