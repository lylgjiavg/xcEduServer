package com.xuecheng.test.fastdfs;

import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Classname TestFastDFS
 * @Description FastDFS 测试类
 * @Date 2020/2/16 17:32
 * @Created by 姜立成
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    //上传文件
    @Test
    public void testUpload() throws IOException, MyException {
        // 设置配置文件
        ClientGlobal.initByProperties("config/fastdfs-client.properties");

        // 获得TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        // 获得TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        // 获得StorageServer
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        // 获得StorageClient1
        StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);

        // 待上传文件路径
        String filePath = "E:\\Document\\Desktop\\timg.jfif";

        String fileId = storageClient1.upload_file1(filePath, "jfif", null);
        // group1/M00/00/00/wKiOgl5JD5mAItRhAABTiIfYqqM48.jfif
        System.out.println(fileId);

    }

    //查询文件
    @Test
    public void testQueryFile() throws IOException, MyException {
        // 设置配置文件
        ClientGlobal.initByProperties("config/fastdfs-client.properties");

        // 获得TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        // 获得TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        // 获得StorageServer
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        // 获得StorageClient1
        StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);

        String fileId = "group1/M00/00/00/wKiOgl5JD5mAItRhAABTiIfYqqM48.jfif";

        FileInfo fileInfo = storageClient1.query_file_info1(fileId);
        System.out.println(fileInfo);
    }

    //下载文件
    @Test
    public void testDownloadFile() throws IOException, MyException {
        // 设置配置文件
        ClientGlobal.initByProperties("config/fastdfs-client.properties");

        // 获得TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        // 获得TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        // 获得StorageServer
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        // 获得StorageClient1
        StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);

        String fileId = "group1/M00/00/00/wKiOgl5JD5mAItRhAABTiIfYqqM48.jfif";

        byte[] bytes = storageClient1.download_file1(fileId);
        IOUtils.write(bytes, new FileOutputStream("e:\\qqM48.jfif"));
    }

}
