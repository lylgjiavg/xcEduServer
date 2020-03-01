package com.xuecheng.api.search;

import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @Classname EsCourseControllerApi
 * @Description 课程搜索 数据接口
 * @Date 2020/3/1 13:20
 * @Created by 姜立成
 */
@Api(value = "课程搜索",description = "课程搜索",tags = {"课程搜索"})
public interface EsCourseControllerApi {

    @ApiOperation("课程搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页数量", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "courseSearchParam", value = "搜索条件", required = false, paramType = "body", dataType = "CourseSearchParam")
    })
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam);

}
