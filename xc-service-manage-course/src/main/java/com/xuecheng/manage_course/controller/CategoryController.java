package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname CategoryController
 * @Description 课程分类 控制层
 * @Date 2020/2/15 17:25
 * @Created by 姜立成
 */
@RestController
@RequestMapping("/category")
public class CategoryController implements CategoryControllerApi {

    @Autowired
    CategoryService categoryService;

    /**
     * 查询课程分类
     * @return 课程分类
     */
    @Override
    @GetMapping("/list")
    public CategoryNode findList() {

        return categoryService.findList();
    }
}
