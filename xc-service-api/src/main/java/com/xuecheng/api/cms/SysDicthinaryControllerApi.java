package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @Classname SysDicthinaryControllerApi
 * @Description 数据字典 数据接口
 * @Date 2020/2/15 18:16
 * @Created by 姜立成
 */
@Api(value = "数据字典接口",description = "提供数据字典接口的管理、查询功能")
public interface SysDicthinaryControllerApi {

    @ApiOperation(value="数据字典查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "字典类型", required = true, paramType = "path", dataType = "String")
    })
    public SysDictionary getByType(String type);
    
}
