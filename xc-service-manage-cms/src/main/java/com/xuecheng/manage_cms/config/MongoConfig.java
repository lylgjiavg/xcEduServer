package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname MongoConfig
 * @Description MongoDB 相关依赖
 * @Date 2020/2/3 19:27
 * @Created by 姜立成
 */
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    String database;

    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(this.database);

        return GridFSBuckets.create(database);
    }

}
