package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Classname TeachplanRepository
 * @Description 课程计划 持久层
 * @Date 2020/2/15 12:51
 * @Created by 姜立成
 */
public interface TeachplanRepository extends JpaRepository<Teachplan, String> {

    public List<Teachplan> findAllByCourseidAndParentid(String courseId, String parentId);

}
