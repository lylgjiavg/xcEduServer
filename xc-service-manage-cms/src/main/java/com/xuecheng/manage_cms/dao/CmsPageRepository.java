package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Classname CmsPageRepository
 * @Description Cms 页面 持久层
 * @Date 2020/1/28 23:42
 * @Created by 姜立成
 */
@Repository
public interface CmsPageRepository extends MongoRepository<CmsPage, String> {

    /**
     * 根据页面名称、站点id、页面访问路径查询
     * @param pageName 页面名称
     * @param siteId 站点id
     * @param pageWebPath 页面访问路径
     * @return 页面信息
     */
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath);
}
