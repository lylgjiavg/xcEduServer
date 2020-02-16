package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Classname CourseMarketRepository
 * @Description 课程营销 持久层
 * @Date 2020/2/15 20:58
 * @Created by 姜立成
 */
public interface CourseMarketRepository extends JpaRepository<CourseMarket, String> {
}
