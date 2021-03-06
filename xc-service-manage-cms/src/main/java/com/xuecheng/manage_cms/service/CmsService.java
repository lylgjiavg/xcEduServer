package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.CustomExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Classname CmsService
 * @Description cms 服务层
 * @Date 2020/2/3 23:18
 * @Created by 姜立成
 */
@Service
public class CmsService {

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsSiteRepository cmsSiteRepository;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 页面静态化
     *
     * @param pageId 页面Id
     * @return 静态化后的HTML字符串
     */
    public String getPageHtml(String pageId) throws IOException, TemplateException {

        // 获得静态化数据
        Map model = this.getModel(pageId);

        // 获得静态化模板
        String templateString = this.getTemplate(pageId);

        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());

        //模板加载器
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate("template", templateString);
        configuration.setTemplateLoader(templateLoader);

        //得到模板
        Template template = configuration.getTemplate("template");

        // 执行静态化
        assert model != null;
        String s = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        return s;
    }

    /**
     * 获得静态化模板
     *
     * @param pageId 页面id
     * @return 静态化模板字符串
     */
    private String getTemplate(String pageId) throws IOException {
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if (optionalCmsPage.isPresent()) {
            CmsPage cmsPage = optionalCmsPage.get();

            // 获得模板id
            String templateId = cmsPage.getTemplateId();
            Optional<CmsTemplate> optionalCmsTemplate = cmsTemplateRepository.findById(templateId);
            if (optionalCmsTemplate.isPresent()) {
                CmsTemplate cmsTemplate = optionalCmsTemplate.get();

                String templateFileId = cmsTemplate.getTemplateFileId();
                GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(templateFileId)));
                GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

                assert gridFSFile != null;
                GridFsResource gridFsResource = new GridFsResource(gridFSFile, downloadStream);
                String template = IOUtils.toString(gridFsResource.getInputStream(), StandardCharsets.UTF_8);

                return template;
            }
        }

        return null;
    }

    /**
     * 根据页面Id获得静态化数据
     *
     * @param pageId 页面Id
     * @return 静态化数据
     */
    private Map getModel(String pageId) {

        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();

            // 获得数据URL
            String dataUrl = cmsPage.getDataUrl();
            ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);


            // 根据url获得不到页面数据
            if (entity.getBody() == null) {
                CustomExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
            }

            return entity.getBody();
        }

        CustomExceptionCast.cast(CmsCode.CMS_COURSE_NOEXISTPAGE);
        return null;
    }


    /**
     * 页面发布
     *
     * @param pageId 页面id
     * @return 页面发布返回信息
     */
    public ResponseResult postPage(String pageId) {
        //执行静态化
        String pageHtml = null;
        try {
            pageHtml = this.getPageHtml(pageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(pageHtml)) {
            CustomExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //保存静态化文件
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //发送消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 发送页面发布消息
     *
     * @param pageId 页面Id
     */
    private void sendPostPage(String pageId) {
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if (!optionalCmsPage.isPresent()) {
            CustomExceptionCast.cast(CmsCode.CMS_COURSE_NOEXISTPAGE);
        }
        CmsPage cmsPage = optionalCmsPage.get();

        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("pageId", pageId);
        //消息内容
        String msg = JSON.toJSONString(msgMap);
        //获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();
        //发布消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, msg);
    }


    /**
     * 保存静态页面内容
     *
     * @param pageId  页面id
     * @param content 页面内容
     * @return 页面内容
     */
    private CmsPage saveHtml(String pageId, String content) {
        //查询页面
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            CustomExceptionCast.cast(CmsCode.CMS_COURSE_NOEXISTPAGE);
        }
        CmsPage cmsPage = optional.get();
        //存储之前先删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        //保存html文件到GridFS
        InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        //文件id
        String fileId = objectId.toString();
        //将文件id存储到cmspage中
        cmsPage.setHtmlFileId(fileId);
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }


    /**
     * 添加页面，如果已存在则更新页面
     *
     * @param cmsPage cms页面信息
     * @return 保存页面返回信息
     */
    public CmsPageResult save(CmsPage cmsPage) {
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage cmsPage1 =
                cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
                        cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmsPage1 != null) {
            //更新
            return this.update(cmsPage1.getPageId(), cmsPage);
        } else {
            //添加
            return this.add(cmsPage);
        }
    }

    /**
     * 添加页面信息
     *
     * @param cmsPage 页面信息
     * @return 返回添加页面信息
     */
    private CmsPageResult add(CmsPage cmsPage) {

        CmsPage save = cmsPageRepository.save(cmsPage);

        return new CmsPageResult(CommonCode.SUCCESS, save);
    }

    /**
     * 更新页面
     *
     * @param pageId  页面id
     * @param cmsPage 页面信息
     * @return 返回更新页面信息
     */
    private CmsPageResult update(String pageId, CmsPage cmsPage) {

        Optional<CmsPage> cmsPageDataBaseOptional = cmsPageRepository.findById(pageId);
        if (cmsPageDataBaseOptional.isPresent()) {
            CmsPage cmsPageDataBase = cmsPageDataBaseOptional.get();
            cmsPageDataBase.setSiteId(cmsPage.getSiteId());
            cmsPageDataBase.setPageName(cmsPage.getPageName());
            cmsPageDataBase.setPageAliase(cmsPage.getPageAliase());
            cmsPageDataBase.setPageWebPath(cmsPage.getPageWebPath());
            cmsPageDataBase.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            cmsPageDataBase.setPageType(cmsPage.getPageType());
            cmsPageDataBase.setPageCreateTime(cmsPage.getPageCreateTime());
            cmsPageDataBase.setTemplateId(cmsPage.getTemplateId());
            cmsPageDataBase.setDataUrl(cmsPage.getDataUrl());

            CmsPage save = cmsPageRepository.save(cmsPageDataBase);

            return new CmsPageResult(CommonCode.SUCCESS, save);
        }

        CustomExceptionCast.cast(CommonCode.FAIL);
        return null;
    }


    /**
     * 一键发布页面
     *
     * @param cmsPage cms页面信息
     * @return cms页面发布信息
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //添加页面
        CmsPageResult save = this.save(cmsPage);
        if (!save.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        CmsPage cmsPage1 = save.getCmsPage();
        //要布的页面id
        String pageId = cmsPage1.getPageId();
        //发布页面
        ResponseResult responseResult = this.postPage(pageId);
        if (!responseResult.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        //得到页面的url
        //页面url=站点域名+站点webpath+页面webpath+页面名称
        //站点id
        String siteId = cmsPage1.getSiteId();
        //查询站点信息
        CmsSite cmsSite = findCmsSiteById(siteId);
        //站点域名
        String siteDomain = cmsSite.getSiteDomain();
        //站点web路径
        String siteWebPath = cmsSite.getSiteWebPath();
        //页面web路径
        String pageWebPath = cmsPage1.getPageWebPath();
        //页面名称
        String pageName = cmsPage1.getPageName();
        //页面的web访问地址
        String pageUrl = siteDomain + siteWebPath + pageWebPath + pageName;
        return new CmsPostPageResult(CommonCode.SUCCESS, pageUrl);
    }

    //根据id查询站点信息
    public CmsSite findCmsSiteById(String siteId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);

        return optional.orElse(null);
    }

}