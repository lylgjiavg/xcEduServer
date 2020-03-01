package com.xuecheng.test.mongodb;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.manage_cms.ManageCmsApplication;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Classname TestGridFS
 * @Description 测试 MongoDB 的 gridfs文件存储
 * @Date 2020/2/3 18:56
 * @Created by 姜立成
 */
@SpringBootTest(classes = ManageCmsApplication.class)
@RunWith(SpringRunner.class)
public class TestGridFS {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Test
    public void store() throws FileNotFoundException {

        String classpath = this.getClass().getResource("/").getPath();
        File file = new File(classpath + "/templates/course.ftl");

        FileInputStream inputStream = new FileInputStream(file);

        ObjectId objectId = gridFsTemplate.store(inputStream, "课程详情模板文件", "utf-8");

        System.out.println(objectId);
    }


    @Test
    public void download() throws IOException {
        String fileId = "5e37ff680b12bc25d034286c";
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));

        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fileId);

        assert gridFSFile != null;
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, downloadStream);

        String html = IOUtils.toString(gridFsResource.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(html);
    }

}
