package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname FileSystemControllerApi
 * @Description 文件系统 数据接口
 * @Date 2020/2/17 23:38
 * @Created by 姜立成
 */
@Api(value = "文件系统管理", description = "提供文件上传、下载、删除、更新")
public interface FileSystemControllerApi {

    @ApiOperation("上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "multipartFile", value = "文件", required = true, dataType = "MultipartFile"),
            @ApiImplicitParam(name = "filetag", value = "文件标签", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "businesskey", value = "业务key", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "metadata", value = "元信息,json格式", required = false, paramType = "query", dataType = "String")
    })
    public UploadFileResult upload(MultipartFile multipartFile,
                                   String filetag,
                                   String businesskey,
                                   String metadata);


}
