package top.codewood.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.codewood.entity.ElasticSearchUtils;
import top.codewood.service.ElasticSearchService;

import java.io.IOException;

@RestController
@RequestMapping("/elastic/rest")
public class ElasticSearchController {

    static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchController.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ElasticSearchService elasticSearchService;

    /**
     * {
     *     "index": "test",
     *     "mappings": {
     *         "dynamic": false,
     *         "properties": {
     *             "id": {
     *                 "type": "integer",
     *                 "index": "false"
     *             },
     *             "name": {
     *                 "type": "text",
     *                 "analyzer": "ik_smart"
     *             }
     *         }
     *     }
     * }
     * @param indexCreateParam
     * @return
     */
    @PostMapping("/create_index")
    public String createIndex(@RequestBody ElasticSearchService.IndexCreateParam indexCreateParam) {
        try {
            if (!elasticSearchService.indexExists(indexCreateParam.getIndex())) {
                LOGGER.info("create index");
                elasticSearchService.createIndex(indexCreateParam.getIndex(), indexCreateParam.getMappings());
            } else {
                return "索引已经存在！";
            }
        } catch (IOException e) {
            return e.getMessage();
        }
        return "ok";
    }

    @DeleteMapping("delete_index")
    public String deleteIndex(@RequestParam("index") String index) {
        elasticSearchService.deleteIndex(index);
        return "ok";
    }

    /**
     * {
     *     "index": "test",
     *     "query": {
     *         "match": {
     *             "name": "哈"
     *         }
     *     }
     * }
     * @param queryParam
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/search")
    public Object search(@RequestBody ElasticSearchService.QueryParam queryParam) throws JsonProcessingException {
        MatchQueryBuilder queryBuilder = null;
        for (String key: queryParam.getQuery().getMatch().keySet()) {
            queryBuilder = QueryBuilders.matchQuery(key, queryParam.getQuery().getMatch().get(key));
        }
        if (queryBuilder != null) {
            return elasticSearchService.search(queryParam.getIndex(), ElasticSearchUtils.searchSourceBuilder(queryBuilder), Object.class);
        }
        return "";
    }

    /**
     * {
     *     "index": "test",
     *     "entity": {
     *         "id": "3",
     *         "data": {
     *          "name": "哈"
     *         }
     *
     *     }
     * }
     * @param insertParam
     * @return
     */
    @RequestMapping("/insert")
    public String insert(@RequestBody ElasticSearchService.InsertParam insertParam) {
        elasticSearchService.insertOrUpdate(insertParam.getIndex(), insertParam.getEntity());
        return "ok";
    }

}
