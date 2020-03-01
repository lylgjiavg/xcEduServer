package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsService;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Classname CmsPagePreviewController
 * @Description 页面预览 控制层
 * @Date 2020/2/4 0:07
 * @Created by 姜立成
 */
@Controller
@RequestMapping("/cms/preview")
public class CmsPagePreviewController extends BaseController {

    @Autowired
    CmsService cmsService;

    //接收到页面id
    @RequestMapping(value = "/{pageId}", method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId) throws IOException, TemplateException {
        response.setHeader("Content‐type","text/html;charset=utf‐8");

        String pageHtml = cmsService.getPageHtml(pageId);
        if (StringUtils.isNotEmpty(pageHtml)) {
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(pageHtml.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
