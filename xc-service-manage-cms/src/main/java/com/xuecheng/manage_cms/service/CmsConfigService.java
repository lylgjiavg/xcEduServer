package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Classname CmsConfigService
 * @Description cms 配置 服务层
 * @Date 2020/2/3 13:11
 * @Created by 姜立成
 */
@Service
public class CmsConfigService {

    private CmsConfigRepository cmsConfigRepository;

    @Autowired
    public CmsConfigService(CmsConfigRepository cmsConfigRepository) {
        this.cmsConfigRepository = cmsConfigRepository;
    }

    public CmsConfig getModel(String id) {

        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);

        return optional.orElse(null);
    }

}
