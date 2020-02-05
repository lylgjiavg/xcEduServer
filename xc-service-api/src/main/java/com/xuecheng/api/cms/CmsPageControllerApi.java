package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @Classname CmsPageControllerApi
 * @Description cms 页面 api
 * @Date 2020/1/28 15:41
 * @Created by 姜立成
 */
@Api(value = "cms 页面管理接口", description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {

    // 页面查询
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);


    @ApiOperation("添加页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cmsPage", value = "页面信息", required = true, paramType = "body", dataType = "CmsPage")
    })
    public CmsPageResult add(CmsPage cmsPage);


    @ApiOperation("根据Id(页面id)查找页面信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "(pageId)页面Id", required = true, paramType = "path", dataType = "String")
    })
    public CmsPage findById(String id);


    @ApiOperation("根据Id(页面id)修改页面信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "(pageId)页面Id", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "cmsPage", value = "页面信息", required = true, paramType = "body", dataType = "CmsPage"),
    })
    public CmsPageResult edit(String id, CmsPage cmsPage);


    @ApiOperation("删除页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "(pageId)页面Id", required = true, paramType = "path", dataType = "String")
    })
    public ResponseResult delete(String id);
}
