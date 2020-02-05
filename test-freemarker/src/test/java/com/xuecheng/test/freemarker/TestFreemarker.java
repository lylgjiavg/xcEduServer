package com.xuecheng.test.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname com.xuecheng.test.freemarker.TestFreemarker
 * @Description Freemarker 静态化测试
 * @Date 2020/2/2 20:28
 * @Created by 姜立成
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFreemarker {

    /**
     * 基于模板生成静态化文件
     *
     * @throws IOException
     * @throws TemplateException
     */
    @Test
    public void test1() throws IOException, TemplateException {

        // 创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());

        // 设置模板路径
        String classPath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classPath + "/templates/"));

        // 加载模板
        Template template = configuration.getTemplate("test1.ftl");

        // 数据模型
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Jiavg");

        // 静态化
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        InputStream inputStream = IOUtils.toInputStream(html);
        FileOutputStream outputStream = new FileOutputStream(new File("D:/test1.html"));

        IOUtils.copy(inputStream, outputStream);
    }


    /**
     * 基于模板字符串生成静态化文件
     * @throws IOException
     * @throws TemplateException
     */
    @Test
    public void testGenerateHtmlByString() throws IOException, TemplateException {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //模板内容，这里测试时使用简单的字符串作为模板
        String templateString = "" +
                "<html>\n" +
                "    <head></head>\n" +
                "    <body>\n" +
                "    名称：${name}\n" +
                "    </body>\n" +
                "</html>";

        //模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", templateString);
        configuration.setTemplateLoader(stringTemplateLoader);

        //得到模板
        Template template = configuration.getTemplate("template", "utf‐8");

        //数据模型
        Map<String, Object> map = new HashMap<>();
        map.put("name", "黑马程序员");

        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //静态化内容
        System.out.println(content);
        InputStream inputStream = IOUtils.toInputStream(content);
        //输出文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/test1.html"));
        IOUtils.copy(inputStream, fileOutputStream);
    }

}
