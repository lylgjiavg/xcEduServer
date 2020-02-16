package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Classname CmsSiteRepository
 * @Description cms 站点 持久层
 * @Date 2020/2/8 0:49
 * @Created by 姜立成
 */
@Repository
public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {
}
