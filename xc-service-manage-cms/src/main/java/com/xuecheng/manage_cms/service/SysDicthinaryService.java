package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDicthinaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname SysDicthinaryService
 * @Description 数据字典 服务层
 * @Date 2020/2/15 18:22
 * @Created by 姜立成
 */
@Service
public class SysDicthinaryService {

    @Autowired
    SysDicthinaryRepository sysDicthinaryRepository;

    /**
     * 数据字典查询
     * @param type 字典类型
     * @return 数据字典
     */
    public SysDictionary getByType(String type) {

        return sysDicthinaryRepository.findByDType(type);
    }

}
