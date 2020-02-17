package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Classname FileSystemRepository
 * @Description 文件系统 持久层
 * @Date 2020/2/17 23:41
 * @Created by 姜立成
 */
public interface FileSystemRepository extends MongoRepository<FileSystem, String> {
}
