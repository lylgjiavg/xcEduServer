package com.xuecheng.test.rabbitmq;

import com.xuecheng.test.rabbitmq.springboot.config.RabbitConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Classname TestRabbit
 * @Description 测试类
 * @Date 2020/2/6 14:46
 * @Created by 姜立成
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRabbit {

    @Autowired
    RabbitTemplate rabbitTemplate;

     @Test
    public void testSendByTopics(){
        for (int i=0;i<5;i++){
            String message = "sms email inform to user"+i;
           
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_TOPICS_INFORM,"inform.sms.email",message);
            System.out.println("Send Message is:'" + message + "'");
        }
    }

}
