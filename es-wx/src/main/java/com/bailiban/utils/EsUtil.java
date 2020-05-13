package com.bailiban.utils;


import com.alibaba.fastjson.JSON;
import com.bailiban.entity.EsEntity;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zhulang
 * @Classname EsUtil
 * @Description TODO
 * @Date 2020/5/12 22:22
 */
@Component
public class EsUtil {
    @Value("${es.host}")
    public String host;
    @Value("${es.port}")
    public int port;
    @Value("${es.scheme}")
    public String scheme;

    public static final String INDEX_NAME = "wx-index";
    public static final String CREATE_INDEX = "{\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"title\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"oaName\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"platform\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"article_type\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"publishTime\" : {\n" +
            "        \"type\": \"date\",\n" +
            "        \"format\": \"yyyy-MM-dd\"\n" +
            "      },\n" +
            "      \"author\" : {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"ignore_above\": 64\n" +
            "      },\n" +
            "      \"viewsCount\" : {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"ignore_above\": 64\n" +
            "      },\n" +
            "      \"shareCount\" : {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"ignore_above\":64\n" +
            "      },\n" +
            "      \"starCount\" : {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"ignore_above\":64\n" +
            "      }\n" +
            "    }\n" +
            "  }";


    public static RestHighLevelClient client = null;


    @PostConstruct
    public void init() {
        try {
            if (client != null) {
                client.close();
            }
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, scheme)));
            if (this.indexExist(INDEX_NAME)) {
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
            request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
            request.mapping(CREATE_INDEX, XContentType.JSON);
            CreateIndexResponse res = client.indices().create(request, RequestOptions.DEFAULT);
            if (!res.isAcknowledged()) {
                throw new RuntimeException("初始化失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * 判断某个index是否存在
     *
     * @param index
     * @return
     * @throws Exception
     */
    public boolean indexExist(String index) throws Exception {
        GetIndexRequest request = new GetIndexRequest(index);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 插入/更新一条记录
     *
     * @param index
     * @param entity
     */
    public void insertOrUpdateOne(String index, EsEntity entity) {
        IndexRequest request = new IndexRequest(index);
        request.id(entity.getId());
        request.source(JSON.toJSONString(entity.getData()), XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量插入数据
     *
     * @param index
     * @param list
     */
    public void insertBatch(String index, List<EsEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(index).id(item.getId())
                .source(JSON.toJSONString(item.getData()), XContentType.JSON)));
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除
     *
     * @param index
     * @param idList
     * @param <T>
     */
    public <T> void deleteBatch(String index, Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(index, item.toString())));
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 搜索
     *
     * @param index
     * @param builder
     * @param c
     * @param <T>
     * @return
     */
    public <T> List<T> search(String index, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request;
        request = new SearchRequest(index);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除index
     *
     * @param index
     */
    public void deleteIndex(String index) {
        try {
            client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * delete by query
     *
     * @param index
     * @param builder
     */
    public void deleteByQuery(String index, QueryBuilder builder) {
        DeleteByQueryRequest request;
        request = new DeleteByQueryRequest(index);
        request.setQuery(builder);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            client.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}