package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.exception.CustomExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

/**
 * @Classname CourseService
 * @Description 课程管理 服务层
 * @Date 2020/2/14 22:41
 * @Created by 姜立成
 */
@Service
public class CourseService {

    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMarketRepository courseMarketRepository;

    /**
     * 课程计划查询
     *
     * @param courseId 课程id
     * @return 课程详情
     */
    public TeachplanNode findTeachplanList(String courseId) {

        return teachplanMapper.selectList(courseId);
    }


    /**
     * 添加课程计划
     *
     * @param teachplan 课程计划
     * @return 添加状态
     */
    public ResponseResult addTeachplan(Teachplan teachplan) {

        // 校验课程id和课程计划名称
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())) {
            CustomExceptionCast.cast(CommonCode.FAIL);
        }

        // 取出课程id
        String courseid = teachplan.getCourseid();
        // 取出父结点id
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            // 如果父结点为空则获取根结点
            parentid = getTeachplanRoot(courseid);
        }
        // 取出父结点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);
        if (!teachplanOptional.isPresent()) {
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        // 父结点
        Teachplan teachplanParent = teachplanOptional.get();
        // 父结点级别
        String parentGrade = teachplanParent.getGrade();
        // 设置父结点
        teachplan.setParentid(parentid);
        teachplan.setStatus("0");//未发布
        // 子结点的级别，根据父结点来判断
        if (parentGrade.equals("1")) {
            teachplan.setGrade("2");
        } else if (parentGrade.equals("2")) {
            teachplan.setGrade("3");
        }
        // 设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());
        teachplanRepository.save(teachplan);

        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 课程列表查询
     *
     * @param page              页码
     * @param size              每页数量
     * @param courseListRequest 查询条件
     * @return 课程列表
     */
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {

        // pageHelper分页
        PageHelper.startPage(page, size);

        Page<CourseInfo> courseInfoPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> courseInfos = courseInfoPage.getResult();

        long total = courseInfoPage.getTotal();

        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setTotal(total);
        queryResult.setList(courseInfos);

        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }


    /**
     * 根据课程id获取课程计划根结点，如果没有则添加根结点
     *
     * @param courseId 课程id
     * @return 课程计划根结点id
     */
    public String getTeachplanRoot(String courseId) {

        // 查询课程是否存在
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(courseId);
        if (!optionalCourseBase.isPresent()) {
            return null;
        }

        CourseBase courseBase = optionalCourseBase.get();

        // 根据课程查询课程计划根节点
        List<Teachplan> teachplans = teachplanRepository.findAllByCourseidAndParentid(courseId, "0");

        // 如果根节点不存在,则添加根节点
        if (teachplans == null || teachplans.size() == 0) {
            Teachplan rootTeachplan = new Teachplan();
            rootTeachplan.setPname(courseBase.getName());
            rootTeachplan.setGrade("1");
            rootTeachplan.setCourseid(courseId);
            rootTeachplan.setStatus("0");
            rootTeachplan.setParentid("0");

            return teachplanRepository.save(rootTeachplan).getId();
        }

        Teachplan teachplan = teachplans.get(0);
        return teachplan.getId();
    }


    /**
     * 获取课程基础信息
     *
     * @param courseId 课程id
     * @return 课程基础信息
     */
    public CourseBase getCourseBaseById(String courseId) {

        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(courseId);

        return optionalCourseBase.orElse(null);
    }


    /**
     * 更新课程基础信息
     *
     * @param id         课程id
     * @param courseBase 课程基础信息
     * @return 更新结果
     */
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {

        // 根据id查询课程基础信息
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(id);
        if (!optionalCourseBase.isPresent()) {
            courseBaseRepository.save(courseBase);
            return new ResponseResult(CommonCode.SUCCESS);
        }

        CourseBase databaseCourseBase = optionalCourseBase.get();
        databaseCourseBase.setName(courseBase.getName());
        databaseCourseBase.setUsers(courseBase.getUsers());
        databaseCourseBase.setMt(courseBase.getMt());
        databaseCourseBase.setSt(courseBase.getSt());
        databaseCourseBase.setGrade(courseBase.getGrade());
        databaseCourseBase.setStudymodel(courseBase.getStudymodel());
        databaseCourseBase.setDescription(courseBase.getDescription());
        courseBaseRepository.save(databaseCourseBase);

        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 获取课程营销信息
     *
     * @param id (同courseId)课程id
     * @return 课程营销信息
     */
    public CourseMarket getCourseMarketById(String id) {

        Optional<CourseMarket> optionalCourseMarket = courseMarketRepository.findById(id);

        return optionalCourseMarket.orElse(null);
    }

    /**
     * 更新课程营销信息
     *
     * @param id           (同courseId)课程id
     * @param courseMarket 课程营销信息
     * @return 更新结果
     */
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {

        // 根据id查询课程营销信息
        Optional<CourseMarket> optionalCourseMarket = courseMarketRepository.findById(id);
        if (!optionalCourseMarket.isPresent()) {
            courseMarketRepository.save(courseMarket);
            return new ResponseResult(CommonCode.SUCCESS);
        }

        CourseMarket databaseCourseMarket = optionalCourseMarket.get();
        databaseCourseMarket.setCharge(courseMarket.getCharge());
        databaseCourseMarket.setValid(courseMarket.getValid());
        databaseCourseMarket.setQq(courseMarket.getQq());
        databaseCourseMarket.setPrice_old(databaseCourseMarket.getPrice());
        databaseCourseMarket.setPrice(courseMarket.getPrice());
        databaseCourseMarket.setStartTime(courseMarket.getStartTime());
        databaseCourseMarket.setEndTime(courseMarket.getEndTime());

        courseMarketRepository.save(databaseCourseMarket);

        return new ResponseResult(CommonCode.SUCCESS);
    }

}
