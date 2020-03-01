package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Classname EsCourseController
 * @Description 课程搜索 控制层
 * @Date 2020/3/1 13:36
 * @Created by 姜立成
 */
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {

    @Autowired
    EsCourseService esCourseService;

    @Override
    @GetMapping(value="/list/{page}/{size}")
    public QueryResponseResult list(@PathVariable("page") int page,@PathVariable("size") int size, CourseSearchParam courseSearchParam) {

        return esCourseService.searchList(page, size, courseSearchParam);
    }
}
