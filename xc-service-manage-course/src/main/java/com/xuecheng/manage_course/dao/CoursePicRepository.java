package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Classname CoursePicRepository
 * @Description 课程图片 持久层
 * @Date 2020/2/18 0:36
 * @Created by 姜立成
 */
public interface CoursePicRepository extends MongoRepository<CoursePic, String> {

    public CoursePic findByCourseid(String courseid);

    public long deleteByCourseid(String courseId);
}
