package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.CustomExceptionCast;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
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
import java.nio.charset.StandardCharsets;
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
    RestTemplate restTemplate;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;

    /**
     * 页面静态化
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




}
