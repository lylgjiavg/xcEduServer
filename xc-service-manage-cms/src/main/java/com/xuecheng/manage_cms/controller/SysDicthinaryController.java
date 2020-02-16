package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDicthinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname SysDicthinaryController
 * @Description 数据字典 控制层
 * @Date 2020/2/15 18:20
 * @Created by 姜立成
 */
@RestController
@RequestMapping("/sys")
public class SysDicthinaryController implements SysDicthinaryControllerApi {

    @Autowired
    SysDicthinaryService sysDicthinaryService;

    /**
     * 数据字典查询
     * @param type 字典类型
     * @return 数据字典
     */
    @Override
    @GetMapping("/dictionary/get/{type}")
    public SysDictionary getByType(@PathVariable("type") String type) {

        return sysDicthinaryService.getByType(type);
    }
}
