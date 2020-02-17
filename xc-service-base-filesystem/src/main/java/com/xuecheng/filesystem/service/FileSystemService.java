package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.CustomExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @Classname FileSystemService
 * @Description 文件系统 服务层
 * @Date 2020/2/17 23:43
 * @Created by 姜立成
 */
@Service
public class FileSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemService.class);
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;
    @Autowired
    FileSystemRepository fileSystemRepository;

    /**
     * 加载fdfs的配置
     */
    private void initFdfsConfig() {

        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            // 初始化文件系统出错
            CustomExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }


    /**
     * 上传文件
     * @param file 文件
     * @param filetag 文件标签
     * @param businesskey 商业标签
     * @param metadata 文件元数据
     * @return 上传结果
     */
    public UploadFileResult upload(MultipartFile file,
                                   String filetag,
                                   String businesskey,
                                   String metadata) {
        if (file == null) {
            CustomExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }

        String fileId = fdfsUpload(file);
        //创建文件信息对象
        FileSystem fileSystem = new FileSystem();
        //文件id
        fileSystem.setFileId(fileId);
        //文件在文件系统中的路径
        fileSystem.setFilePath(fileId);
        //业务标识
        fileSystem.setBusinesskey(businesskey);
        //标签
        fileSystem.setFiletag(filetag);
        //元数据
        if(StringUtils.isNotEmpty(metadata)){
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //名称
        fileSystem.setFileName(file.getOriginalFilename());
        //大小
        fileSystem.setFileSize(file.getSize());
        //文件类型
        fileSystem.setFileType(file.getContentType());
        fileSystemRepository.save(fileSystem);

        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
    }


    /**
     * 上传文件到fdfs，返回文件id
     * @param file 文件
     * @return 文件id
     */
    public String fdfsUpload(MultipartFile file) {

        // 初始化fdfs环境
        initFdfsConfig();

        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();

            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);

            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            int indexOf = originalFilename.lastIndexOf(".");
            String ext = originalFilename.substring(indexOf + 1);

            String fileId = storageClient1.upload_file1(fileBytes, ext, null);

            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
            CustomExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }

        return null;
    }

}