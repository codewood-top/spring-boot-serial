package top.codewood.entity;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.concurrent.TimeUnit;

public class ElasticSearchUtils {

    /**
     *
     * @param queryBuilder 设置查询对象
     * @param from 开始搜索的结果索引
     * @param size 返回的搜索匹配数
     * @param timeout
     * @return
     */
    public static SearchSourceBuilder searchSourceBuilder(QueryBuilder queryBuilder, int from, int size, int timeout) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.from(from);
        sourceBuilder.size(size);
        sourceBuilder.timeout(new TimeValue(timeout, TimeUnit.SECONDS));
        return sourceBuilder;
    }

    public static SearchSourceBuilder searchSourceBuilder(QueryBuilder queryBuilder) {
        return searchSourceBuilder(queryBuilder, 0 , 10, 60);
    }

}
