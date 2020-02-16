package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @Classname CategoryControllerApi
 * @Description 课程分类 数据接口
 * @Date 2020/2/15 17:23
 * @Created by 姜立成
 */
@Api(value = "课程分类管理", description = "课程分类管理")
public interface CategoryControllerApi {

    @ApiOperation("查询课程分类")
    public CategoryNode findList();

}
