package top.codewood.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.codewood.entity.ElasticSearchEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ElasticSearchService {

    static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchService.class);

    private ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
    }

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     *
     * @param idxName 索引名称
     * @param mappings
     */
    public void createIndex(String idxName, Mappings mappings) {
        try {
            if (this.indexExists(idxName)) {
                LOGGER.error("idxName: {} 已经存在， idsSql: {}", idxName, mappings.toString());
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(idxName);
            this.buildSetting(request);
            //request.mapping(properties, XContentType.JSON);
            request.mapping(objectMapper.convertValue(mappings, Map.class));

            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

            if (!response.isAcknowledged()) {
                throw new RuntimeException(String.format("未能创建索引：%s", idxName));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param idxName
     * @return
     * @throws IOException
     */
    public boolean indexExists(String idxName) throws IOException {
        return restHighLevelClient.indices().exists(new GetIndexRequest(idxName), RequestOptions.DEFAULT);
    }

    /**
     * 制定配置项的判断索引是否存在
     * @param idxName
     * @return
     * @throws IOException
     */
    public boolean indexExistsWithOptions(String idxName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(idxName);
        // true: 返回本地信息检索状态， false: 还是从主节点检索状态
        request.local(false);
        // 是否适应人类查看的格式
        request.humanReadable(true);
        // 是否为每个索引返回所有的默认设置
        request.includeDefaults(false);

        // 控制如何解决不可用的索引以及如何扩展通配符表达式，忽略不可用索引的索引选项，仅将统配符扩展为开放索引，并且不允许从通配符表达式解析任何索引
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 索引分片设置
     * @param request
     */
    public void buildSetting(CreateIndexRequest request) {
        request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
    }

    public void insertOrUpdate(String idxName, ElasticSearchEntity entity) {
        IndexRequest request = new IndexRequest(idxName);
        request.id(entity.getId());
        try {
            request.source(objectMapper.writeValueAsString(entity.getData()), XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void delete(String idxName, ElasticSearchEntity entity) {
        DeleteRequest request = new DeleteRequest(idxName);
        request.id(entity.getId());
        try {
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(String idxName, List<ElasticSearchEntity> entityList) {
        BulkRequest request = new BulkRequest();

        try {

            for (ElasticSearchEntity entity : entityList) {
                request.add(new IndexRequest(idxName).id(entity.getId()).source(objectMapper.writeValueAsString(entity.getData()), XContentType.JSON));
            }
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String idxName, Collection<String> ids) {
        BulkRequest request = new BulkRequest();
        ids.forEach(id -> request.add(new DeleteRequest(idxName, id)));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> search(String idxName, SearchSourceBuilder builder, Class<T> clazz) {
        SearchRequest request = new SearchRequest(idxName);
        request.source(builder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(objectMapper.convertValue(hit.getSourceAsString(), clazz));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteIndex(String idxName) {
        try {
            if (!this.indexExists(idxName)) {
                LOGGER.error("删除失败, 不存在idxName：{}", idxName);
                return;
            }
            restHighLevelClient.indices().delete(new DeleteIndexRequest(idxName), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByQuery(String idxName, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(idxName);
        request.setQuery(builder);

        // 设置批量操作数量， 最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");

        try {
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static class IndexCreateParam {
        private String index;

        private Mappings mappings;
        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }


        public Mappings getMappings() {
            return mappings;
        }

        public void setMappings(Mappings mappings) {
            this.mappings = mappings;
        }
    }

    public static class Mappings {
        private boolean dynamic = false;
        private Map<String, Map<String, Object>> properties;

        public boolean isDynamic() {
            return dynamic;
        }

        public void setDynamic(boolean dynamic) {
            this.dynamic = dynamic;
        }

        public Map<String, Map<String, Object>> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Map<String, Object>> properties) {
            this.properties = properties;
        }
    }

    public static class QueryParam {
        private String index;
        // {"query": {"match": {"name": "xx"}}}
        private Query query;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public Query getQuery() {
            return query;
        }

        public void setQuery(Query query) {
            this.query = query;
        }
    }

    public static class Query {
        private Map<String, Object> match;

        public Map<String, Object> getMatch() {
            return match;
        }

        public void setMatch(Map<String, Object> match) {
            this.match = match;
        }
    }

    public static class InsertParam {
        private String index;
        private ElasticSearchEntity entity;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public ElasticSearchEntity getEntity() {
            return entity;
        }

        public void setEntity(ElasticSearchEntity entity) {
            this.entity = entity;
        }
    }

}
