package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Classname CmsConfigRepository
 * @Description cms 配置 持久层
 * @Date 2020/2/3 13:12
 * @Created by 姜立成
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig, String> {
}
