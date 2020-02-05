package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @Classname CmsConfigControllerApi
 * @Description cms 配置 api
 * @Date 2020/2/3 13:03
 * @Created by 姜立成
 */
@Api(value = "cms配置管理接口", description = "cms配置管理接口，提供数据模型的管理、查询接口")
public interface CmsConfigControllerApi {

    @ApiOperation("根据id查询CMS配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "页面配置Id", required = true, paramType = "path", dataType = "String")
    })
    public CmsConfig getModel(String id);

}
