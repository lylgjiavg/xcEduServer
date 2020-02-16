package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @Classname CourseControllerApi
 * @Description 课程管理 数据接口
 * @Date 2020/2/8 17:58
 * @Created by 姜立成
 */
@Api(value = "课程管理服务", description = "课程管理服务,提供课程管理的增、删、改、查")
public interface CourseControllerApi {

    @ApiOperation("课程计划查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程Id", required = true, paramType = "path", dataType = "String")
    })
    public TeachplanNode findTeachplanList(String courseId);


    @ApiOperation("添加课程计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teachplan", value = "课程计划", required = true, paramType = "body", dataType = "Teachplan")
    })
    public ResponseResult addTeachplan(Teachplan teachplan);


    @ApiOperation("课程列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页数量", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "courseListRequest", value = "查询条件",paramType = "query", dataType = "CourseListRequest")
    })
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);


    @ApiOperation("获取课程基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true, paramType = "path", dataType = "String")
    })
    public CourseBase getCourseBaseById(String courseId);


    @ApiOperation("更新课程基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程id", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "courseBase", value = "课程基础信息", required = true, paramType = "body", dataType = "CourseBase")
    })
    public ResponseResult updateCourseBase(String id, CourseBase courseBase);


    @ApiOperation("获取课程营销信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "(同courseId)课程id", required = true, paramType = "path", dataType = "String")
    })
    public CourseMarket getCourseMarketById(String id);


    @ApiOperation("更新课程营销信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "(同courseId)课程id", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "courseMarket", value = "课程营销信息", required = true, paramType = "body", dataType = "CourseMarket")
    })
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);

}
