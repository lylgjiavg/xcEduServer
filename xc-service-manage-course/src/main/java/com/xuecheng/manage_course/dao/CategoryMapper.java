package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Classname CategoryMapper
 * @Description 课程分类 持久层
 * @Date 2020/2/15 17:30
 * @Created by 姜立成
 */
@Mapper
public interface CategoryMapper {

    /**
     * 查询课程分类
     * @return 课程分类
     */
    public CategoryNode findList();

}
