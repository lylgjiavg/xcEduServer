package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sun.org.apache.regexp.internal.RE;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.CustomExceptionCast;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @Classname PageService
 * @Description 页面服务
 * @Date 2020/2/8 0:44
 * @Created by 姜立成
 */
@Service
public class PageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsSiteRepository cmsSiteRepository;

    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    GridFsTemplate gridFsTemplate;

    /**
     * 将页面html保存到页面物理路径
     *
     * @param pageId
     */
    public void savePageToServerPath(String pageId) {

        // 根据页面id查询页面信息
        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(pageId);
        if (!cmsPageOptional.isPresent()) {
            CustomExceptionCast.cast(CmsCode.CMS_COURSE_NOEXISTPAGE);
        }

        CmsPage cmsPage = cmsPageOptional.get();

        // 根据页面信息中站点id查找站点信息
        String siteId = cmsPage.getSiteId();
        CmsSite cmsSite = this.getCmsSiteById(siteId);

        // 根据页面信息和站点信息,组合得到页面物理路径
        String pagePagePhysicalPath = cmsPage.getPagePhysicalPath();
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();

        String pagePhysicalPath = sitePhysicalPath + pagePagePhysicalPath;

        // 根据页面信息的文件id获取文件内容
        InputStream inputStream = this.getFileById(cmsPage.getHtmlFileId());

        // 把文件保存到本地
        File file = new File(pagePhysicalPath + cmsPage.getPageName());
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据文件id获取文件内容
     *
     * @param fileId 文件id
     * @return 文件输入流
     */
    public InputStream getFileById(String fileId) {

        GridFSFile fsFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        assert fsFile != null;
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(fsFile.getObjectId());

        GridFsResource gridFsResource = new GridFsResource(fsFile, gridFSDownloadStream);

        InputStream inputStream = null;
        try {
            inputStream = gridFsResource.getInputStream();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        return inputStream;
    }


    /**
     * 根据站点id得到站点
     *
     * @param siteId 站点id
     * @return 站点信息
     */
    public CmsSite getCmsSiteById(String siteId) {

        Optional<CmsSite> optionalCmsSite = cmsSiteRepository.findById(siteId);
        if (!optionalCmsSite.isPresent()) {
            CustomExceptionCast.cast(CmsCode.CMS_SITE_NOEXISTPAGE);
        }

        return optionalCmsSite.get();
    }

}
