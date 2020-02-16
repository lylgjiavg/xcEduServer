package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Classname CmsPageRepository
 * @Description cms 页面 持久层
 * @Date 2020/2/8 0:48
 * @Created by 姜立成
 */
@Repository
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {
}
