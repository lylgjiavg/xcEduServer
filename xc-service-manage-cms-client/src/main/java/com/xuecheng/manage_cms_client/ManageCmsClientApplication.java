package com.xuecheng.manage_cms_client;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Classname ManageCmsClientApplication
 * @Description ManageCmsClient 启动类
 * @Date 2020/2/8 0:30
 * @Created by 姜立成
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描实体类
@ComponentScan(basePackages={"com.xuecheng.framework"})//扫描common下的所有类
@ComponentScan(basePackages={"com.xuecheng.manage_cms_client"})
public class ManageCmsClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClientApplication.class, args);
    }

    @Bean
    public GridFSBucket gridFSBucket(@Value("${spring.data.mongodb.database}") String database) {
        MongoClient client = new MongoClient();
        MongoDatabase mongoDatabase = client.getDatabase(database);

        return GridFSBuckets.create(mongoDatabase);
    }

}
