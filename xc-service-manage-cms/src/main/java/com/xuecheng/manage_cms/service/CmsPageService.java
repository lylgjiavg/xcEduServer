package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Classname CmsPageService
 * @Description Cms 页面 服务层
 * @Date 2020/1/28 23:43
 * @Created by 姜立成
 */
@Service
public class CmsPageService {

    @Autowired
    CmsPageRepository cmsPageRepository;

    /**
     * Cms 页面分页查询
     * @param page             页面索引,从1开始
     * @param size             页面记录数
     * @param queryPageRequest 页面查询条件
     * @return 查询到的页面数据
     */
    public QueryResponseResult findList(int page,int size, QueryPageRequest queryPageRequest) {

        // 处理非法情况
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }

        // 查询条件
        CmsPage cmsPage = new CmsPage();
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        if (StringUtils.isNotEmpty(queryPageRequest.getPageName())) {
            // 模糊查询 页面名称
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            // 页面别名
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            // 站点ID
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

        // 分页查询
        Pageable pageable = PageRequest.of(page, size);

        // 根据条件查询页面信息
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        long totalPages = all.getTotalElements();
        List<CmsPage> cmsPages = all.getContent();

        // 封装查询到的页面信息到响应中
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setTotal(totalPages);
        queryResult.setList(cmsPages);

        QueryResponseResult responseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);

        return responseResult;
    }

    /**
     * Cms 添加页面
     * @param cmsPage 页面信息
     * @return 添加返回信息
     */
    public CmsPageResult add(CmsPage cmsPage) {

        CmsPageResult cmsPageResult;

        // 确保页面Id为空 (然后由数据库生成)
        cmsPage.setPageId(null);

        // 根据索引查询页面信息是否已存在
        String siteId = cmsPage.getSiteId();
        String pageName = cmsPage.getPageName();
        String pageWebPath = cmsPage.getPageWebPath();
        CmsPage dbPage = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(pageName, siteId, pageWebPath);

        // 数据库不存在页面信息
        if (dbPage == null) {

            CmsPage savePage = cmsPageRepository.save(cmsPage);

            cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, savePage);
            return cmsPageResult;
        }

        // 数据库已存在页面信息
        cmsPageResult = new CmsPageResult(CommonCode.FAIL, null);
        return cmsPageResult;
    }

    /**
     * 根据Id(页面id)查找页面信息
     *
     * @param id (pageId)页面Id
     * @return 页面信息
     */
    public CmsPage findById(String id) {

        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }

        return null;
    }

    /**
     * 根据Id(页面id)修改页面信息
     * @param id      (pageId)页面Id
     * @param cmsPage 页面信息
     * @return 修改后的页面信息
     */
    public CmsPageResult edit(String id, CmsPage cmsPage) {
        CmsPageResult cmsPageResult;

        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            CmsPage dbPage = optional.get();

            dbPage.setSiteId(cmsPage.getSiteId());
            dbPage.setTemplateId(cmsPage.getTemplateId());
            dbPage.setPageName(cmsPage.getPageName());
            dbPage.setPageAliase(cmsPage.getPageAliase());
            dbPage.setPageWebPath(cmsPage.getPageWebPath());
            dbPage.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            dbPage.setDataUrl(cmsPage.getDataUrl());
            dbPage.setPageType(cmsPage.getPageType());
            dbPage.setPageCreateTime(cmsPage.getPageCreateTime());

            CmsPage savePage = cmsPageRepository.save(dbPage);
            cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, savePage);

            return cmsPageResult;
        }

        cmsPageResult = new CmsPageResult(CommonCode.FAIL, null);
        return cmsPageResult;
    }

    /**
     * 删除页面
     * @param id (pageId)页面Id
     * @return 删除返回后信息
     */
    public ResponseResult delete(String id) {
        ResponseResult responseResult;

        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            cmsPageRepository.deleteById(id);

            responseResult = new ResponseResult(CommonCode.SUCCESS);
            return responseResult;
        }

        responseResult = new ResponseResult(CommonCode.FAIL);
        return responseResult;
    }
}
