package com.xuecheng.govern.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Classname GovernCenterApplication
 * @Description 注册中心 启动类
 * @Date 2020/2/19 13:37
 * @Created by 姜立成
 */
@EnableEurekaServer //标识这是一个Eureka服务
@SpringBootApplication
public class GovernCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernCenterApplication.class, args);
    }

}
