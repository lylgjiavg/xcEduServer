package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.CourseView;
import com.xuecheng.framework.exception.CustomExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.feign.CmsPageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    CoursePubRepository coursePubRepository;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CmsPageClient cmsPageClient;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;


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

    /**
     * 添加课程图片
     *
     * @param courseId 课程id
     * @param pic      课程图片id
     * @return 响应信息
     */
    @Transactional
    public ResponseResult saveCoursePic(String courseId, String pic) {

        // 查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (picOptional.isPresent()) {
            coursePic = picOptional.get();
        }
        //没有课程图片则新建对象
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        //保存课程图片
        coursePicRepository.save(coursePic);

        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 获取课程图片信息
     *
     * @param courseId 课程id
     * @return 课程图片信息
     */
    public CoursePic findCoursepic(String courseId) {

        return coursePicRepository.findByCourseid(courseId);
    }


    /**
     * 删除课程图片
     *
     * @param courseId 课程id
     * @return 响应结果
     */
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        //执行删除，返回1表示删除成功，返回0表示删除失败
        long result = coursePicRepository.deleteByCourseid(courseId);
        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 课程视图查询
     *
     * @param id 课程id
     * @return 课程视图信息
     */
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();
        //查询课程基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()) {
            CourseMarket courseMarket = courseMarketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        //查询课程图片信息
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(picOptional.get());
        }
        //查询课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    /**
     * 课程预览
     *
     * @param courseId 课程id
     * @return 课程预览结果
     */
    public CoursePublishResult preview(String courseId) {
        CourseBase one = this.findCourseBaseById(courseId);
        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面别名
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        //远程请求cms保存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        //页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //页面url
        String pageUrl = previewUrl + pageId;
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    //根据id查询课程基本信息
    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if (baseOptional.isPresent()) {
            return baseOptional.get();
        }
        CustomExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }


    /**
     * 发布课程
     *
     * @param courseId 课程id
     * @return 发布课程信息
     */
    @Transactional
    public CoursePublishResult publish(String courseId) {
        //课程信息
        CourseBase one = this.findCourseBaseById(courseId);
        //发布课程详情页面
        CmsPostPageResult cmsPostPageResult = publish_page(courseId);
        if (!cmsPostPageResult.isSuccess()) {
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        //更新课程状态
        CourseBase courseBase = saveCoursePubState(courseId);
        //课程索引...
        //课程缓存...
        CoursePub coursePub = createCoursePub(courseId);
        //向数据库保存课程索引信息
        CoursePub newCoursePub = saveCoursePub(courseId, coursePub);
        if (newCoursePub == null) {
            //创建课程索引信息失败
            CustomExceptionCast.cast(CourseCode.COURSE_PUBLISH_CREATE_INDEX_ERROR);
        }

        //页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }



    /**
     * 创建coursePub对象
     *
     * @param id 课程id
     * @return coursePub对象
     */
    private CoursePub createCoursePub(String id) {
        CoursePub coursePub = new CoursePub();
        coursePub.setId(id);
        //基础信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase, coursePub);
        }
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()) {
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        //将课程计划转成json
        String teachplanString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachplanString);
        return coursePub;
    }


    /**
     * 保存CoursePub
     *
     * @param id        课程id
     * @param coursePub 课程发布信息
     * @return 保存的课程发布信息
     */
    public CoursePub saveCoursePub(String id, CoursePub coursePub) {
        if (StringUtils.isNotEmpty(id)) {
            CustomExceptionCast.cast(CommonCode.FAIL);
        }

        CoursePub coursePubDB = null;
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()) {
            coursePubDB = coursePubOptional.get();
        } else {
            coursePubDB = new CoursePub();
        }

        BeanUtils.copyProperties(coursePub, coursePubDB);
        //设置主键
        coursePubDB.setId(id);
        //更新时间戳为最新时间
        coursePub.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePub.setPubTime(date);
        coursePubRepository.save(coursePub);

        return coursePub;
    }

    //更新课程发布状态
    private CourseBase saveCoursePubState(String courseId) {
        CourseBase courseBase = this.findCourseBaseById(courseId);
        //更新发布状态
        courseBase.setStatus("202002");
        return courseBaseRepository.save(courseBase);
    }

    //发布课程正式页面
    public CmsPostPageResult publish_page(String courseId) {
        CourseBase one = this.findCourseBaseById(courseId);
        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面别名
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        //发布页面
        return cmsPageClient.postPageQuick(cmsPage);
    }

}
