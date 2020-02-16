package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Classname CourseMapper
 * @Description 课程管理 持久层
 * @Date 2020/2/8 18:58
 * @Created by 姜立成
 */
@Mapper
public interface CourseMapper {

   CourseBase findCourseBaseById(String id);

   Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);

}
