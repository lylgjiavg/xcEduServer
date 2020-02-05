package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname CmsConfigController
 * @Description cms 配置 控制层
 * @Date 2020/2/3 13:08
 * @Created by 姜立成
 */
@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {

    private CmsConfigService cmsConfigService;

    public CmsConfigController(CmsConfigService cmsConfigService) {
        this.cmsConfigService = cmsConfigService;
    }

    /**
     * 根据id查询CMS配置信息
     * @param id 页面配置id
     * @return 页面配置信息
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsConfig getModel(@PathVariable("id") String id) {

        return cmsConfigService.getModel(id);
    }
}
