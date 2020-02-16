package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname CategoryService
 * @Description 课程分类 服务层
 * @Date 2020/2/15 17:27
 * @Created by 姜立成
 */
@Service
public class CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 查询课程分类
     * @return 课程分类
     */
    public CategoryNode findList() {

        return categoryMapper.findList();
    }

}
