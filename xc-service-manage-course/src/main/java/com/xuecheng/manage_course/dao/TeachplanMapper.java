package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Classname TeachplanMapper
 * @Description 课程计划 持久层
 * @Date 2020/2/14 22:57
 * @Created by 姜立成
 */
@Mapper
public interface TeachplanMapper {

    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * @return 课程计划
     */
    public TeachplanNode selectList(String courseId);

}
