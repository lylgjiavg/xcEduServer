package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsPageService;
import com.xuecheng.manage_cms.service.CmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Classname CmsPageController
 * @Description cms 页面 控制层
 * @Date 2020/1/28 17:13
 * @Created by 姜立成
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    CmsPageService cmsPageService;
    @Autowired
    CmsService cmsService;

    /**
     * Cms 页面分页查询
     * @param page 页面索引
     * @param size 页面记录数
     * @param queryPageRequest 页面查询条件
     * @return 查询到的页面数据
     */
    @Override
    @GetMapping("list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {

        return cmsPageService.findList(page, size, queryPageRequest);
    }

    /**
     * Cms 添加页面
     * @param cmsPage 页面信息
     * @return 添加返回信息
     */
    @Override
    @PostMapping("add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {

        return cmsPageService.add(cmsPage);
    }

    /**
     * 根据Id(页面id)查找页面信息
     * @param id (pageId)页面Id
     * @return 页面信息
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {

        return cmsPageService.findById(id);
    }

    /**
     * 根据Id(页面id)修改页面信息
     * @param id (pageId)页面Id
     * @param cmsPage 页面信息
     * @return 修改后的页面信息
     */
    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {

        return cmsPageService.edit(id, cmsPage);
    }

    /**
     * 删除页面
     * @param id (pageId)页面Id
     * @return 删除返回后信息
     */
    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {

        return cmsPageService.delete(id);
    }

    /**
     * 发布页面
     * @param pageId 页面Id
     * @return 发布页面信息
     */
    @Override
    @GetMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {

        return cmsService.postPage(pageId);
    }

    /**
     * 保存页面
     * @param cmsPage cms页面信息
     * @return 保存页面返回信息
     */
    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return cmsService.save(cmsPage);
    }


    /**
     * 一键发布页面
     * @param cmsPage cms页面信息
     * @return cms页面发布信息
     */
    @Override
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        return cmsService.postPageQuick(cmsPage);
    }
}
