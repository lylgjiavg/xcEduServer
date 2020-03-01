package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Classname EsCourseService
 * @Description 课程搜索 服务层
 * @Date 2020/3/1 13:34
 * @Created by 姜立成
 */
@Service
public class EsCourseService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Value("${elasticsearch.course.type}")
    String type;
    @Value("${elasticsearch.course.index}")
    String index;
    @Value("${elasticsearch.course.source_field}")
    String sourceField;


    public QueryResponseResult searchList(int page, int size, CourseSearchParam courseSearchParam) {

        // 设置索引
        SearchRequest searchRequest = new SearchRequest(index);
        // 设置类型
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        String[] includes = sourceField.split(",");
        // source源字段过滤
        searchSourceBuilder.fetchSource(includes, new String[]{});
        // 添加关键字查询
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
            // 匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                    .multiMatchQuery(courseSearchParam.getKeyword(), "name", "teachplan", "description");
            // 设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            // 提升另个字段的Boost值
            multiMatchQueryBuilder.field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        // 添加分类查询
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("mt", courseSearchParam.getMt());
            boolQueryBuilder.filter(termQuery);
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("st", courseSearchParam.getSt());
            boolQueryBuilder.filter(termQuery);
        }

        // 添加难度查询
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            TermQueryBuilder termQuery = QueryBuilders.termQuery("grade", courseSearchParam.getGrade());
            boolQueryBuilder.filter(termQuery);
        }

        // 分页
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 20;
        }
        int start = (page - 1) * size;
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(size);

        // 布尔查询
        searchSourceBuilder.query(boolQueryBuilder);

        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        // 请求搜索
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return new QueryResponseResult(CommonCode.SUCCESS, new QueryResult<CoursePub>());
        }

        // 结果集处理
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        // 记录总数
        long totalHits = hits.getTotalHits();
        // 数据列表
        List<CoursePub> list = new ArrayList<>();

        for (SearchHit searchHit : searchHits) {
            CoursePub coursePub = new CoursePub();

            // 取出source
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            // 取出名称
            String name = (String) sourceAsMap.get("name");
            coursePub.setName(name);
            // 图片
            String pic = (String) sourceAsMap.get("pic");
            coursePub.setPic(pic);
            // 价格
            Double price = null;
            try {
                if (sourceAsMap.get("price") != null) {
                    price = (Double) sourceAsMap.get("price");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            coursePub.setPrice(price);
            Double price_old = null;
            try {
                if (sourceAsMap.get("price_old") != null) {
                    price_old = (Double) sourceAsMap.get("price_old");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            coursePub.setPrice_old(price_old);
            list.add(coursePub);
        }

        QueryResult<CoursePub> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(totalHits);
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

}
