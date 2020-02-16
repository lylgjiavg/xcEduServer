package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Classname CourseController
 * @Description 课程管理 控制层
 * @Date 2020/2/14 22:39
 * @Created by 姜立成
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;

    /**
     * 课程计划查询
     * @param courseId 课程id
     * @return 课程详情
     */
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {

        return courseService.findTeachplanList(courseId);
    }

    /**
     * 添加课程计划
     * @param teachplan 课程计划
     * @return 添加状态
     */
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {

        return courseService.addTeachplan(teachplan);
    }

    /**
     * 课程列表查询
     * @param page 页码
     * @param size 每页数量
     * @param courseListRequest 查询条件
     * @return 课程列表
     */
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {

        return courseService.findCourseList(page, size, courseListRequest);
    }


    /**
     * 获取课程基础信息
     * @param courseId 课程id
     * @return 课程基础信息
     */
    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) {

        return courseService.getCourseBaseById(courseId);
    }


    /**
     * 更新课程基础信息
     * @param id 课程id
     * @param courseBase 课程基础信息
     * @return 更新结果
     */
    @Override
    @PostMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {

        return courseService.updateCourseBase(id, courseBase);
    }


    /**
     * 获取课程营销信息
     * @param id (同courseId)课程id
     * @return 课程营销信息
     */
    @Override
    @GetMapping("/courseMarket/get/{id}")
    public CourseMarket getCourseMarketById(@PathVariable("id") String id) {

        return courseService.getCourseMarketById(id);
    }

    /**
     * 更新课程营销信息
     * @param id (同courseId)课程id
     * @param courseMarket 课程营销信息
     * @return 更新结果
     */
    @Override
    @PostMapping("/courseMarket/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {

        return courseService.updateCourseMarket(id, courseMarket);
    }

}
