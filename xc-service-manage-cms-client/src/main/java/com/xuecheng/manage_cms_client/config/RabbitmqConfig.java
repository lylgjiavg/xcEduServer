package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname RabbitmqConfig
 * @Description Rabbitmq 配置类
 * @Date 2020/2/8 0:31
 * @Created by 姜立成
 */
@Configuration
public class RabbitmqConfig {

    // 队列bean的名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    // 交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";
    // 队列的名称
    @Value("${xuecheng.mq.queue}")
    public String queue_cms_postpage_name;
    // routingKey 即站点Id
    @Value("${xuecheng.mq.routingKey}")
    public String routingKey;

    /**
     * 声明交换机 [direct类型]
     * @return 交换机对象
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE() {
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    /**
     * 声明队列
     * @return 队列对象
     */
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE() {
        return new Queue(queue_cms_postpage_name);
    }

    /**
     * 绑定队列到交换机
     * @param queue 队列
     * @param exchange 交换机
     * @return 绑定对象
     */
    @Bean
    public Binding binding(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue, @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }

}
