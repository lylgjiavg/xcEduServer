package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Classname CmsTemplateRepository
 * @Description cms 模板 持久层
 * @Date 2020/2/3 23:42
 * @Created by 姜立成
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate, String> {
}
