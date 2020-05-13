package com.bailiban.service;

import com.bailiban.entity.EsEntity;
import com.bailiban.entity.Item;
import com.bailiban.utils.EsUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhulang
 * @Classname ItemService
 * @Description TODO
 * @Date 2020/5/12 22:30
 */
@Service
public class ItemService {
    @Autowired
    private EsUtil esUtil;

    /**
     * @param id 获取某一个
     */
    public Item getById(int id) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(new TermQueryBuilder("id", id));
        List<Item> res = esUtil.search(EsUtil.INDEX_NAME, builder, Item.class);
        if (res.size() > 0) {
            return res.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取全部
     */
    public List<Item> getAll() {
        return esUtil.search(EsUtil.INDEX_NAME, new SearchSourceBuilder().size(10000), Item.class);
    }

    /**
     * 根据关键词搜索某部门的某文章
     *
     * @param dep ,title
     */
    public List<Item> searchByDepAndTitle(String dep, String title) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("department", dep));
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        SearchSourceBuilder builder = new SearchSourceBuilder();
        SearchSourceBuilder query = builder.size(10).query(boolQueryBuilder);
        return esUtil.search(EsUtil.INDEX_NAME, query, Item.class);

    }

    /**
     * 根据作者姓名，平台查询指定日期范围内的元素
     *
     * @param author
     * @param platform
     * @param startTime
     * @param endTime
     * @return
     */
    public List<Item> searchByAuthorAndPlateformAndPublishTime(String author, String platform, String startTime, String endTime) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("author", author));
        boolQueryBuilder.must(QueryBuilders.termQuery("platform", platform));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishTime").format("yyyy-MM-dd").from(startTime).to(endTime));
        SearchSourceBuilder builder = new SearchSourceBuilder();
        SearchSourceBuilder query = builder.size(1000).query(boolQueryBuilder).sort("publishTime");
        return esUtil.search(EsUtil.INDEX_NAME, query, Item.class);
    }

    /**
     * 单个插入
     *
     * @param item item
     */
    public void putOne(Item item) {
        EsEntity<Item> entity = new EsEntity<>(item.getId().toString(), item);
        esUtil.insertOrUpdateOne(EsUtil.INDEX_NAME, entity);
    }

    /**
     * 批量插入
     *
     * @param itemList itemList
     */
    public void putList(List<Item> itemList) {
        List<EsEntity> list = new ArrayList<>();
        itemList.forEach(item -> list.add(new EsEntity<>(item.getId().toString(), item)));
        esUtil.insertBatch(EsUtil.INDEX_NAME, list);
    }

    /**
     * 批量删除
     *
     * @param list list
     */
    public void deleteBatch(List<Integer> list) {
        esUtil.deleteBatch(EsUtil.INDEX_NAME, list);
    }

    /**
     * delete by query 根据用户姓名删除数据
     *
     * @param author author
     */
    public void deleteByAuthor(String author) {
        esUtil.deleteByQuery(EsUtil.INDEX_NAME, new TermQueryBuilder("author", author));
    }

    public void deleteIndex(String index) {
        esUtil.deleteIndex(index);
    }
}
