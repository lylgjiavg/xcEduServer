package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Classname SysDicthinaryRepository
 * @Description 数据字典 持久层
 * @Date 2020/2/15 18:25
 * @Created by 姜立成
 */
public interface SysDicthinaryRepository extends MongoRepository<SysDictionary, String> {

    /**
     * 数据字典查询
     * @param dType 字典类型
     * @return 数据字典
     */
    public SysDictionary findByDType(String dType);

}
