package com.bailiban.controller;

import com.bailiban.entity.Item;
import com.bailiban.repository.WxRepository;
import com.bailiban.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * @author zhulang
 * @Classname ItemController
 * @Description TODO
 * @Date 2020/5/12 22:48
 */
@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private WxRepository wxRepository;

    /**
     * @param id 获取某一个
     */
    @GetMapping("/{id}")
    public Item getById(@PathVariable("id") int id) {
        return itemService.getById(id);
    }

    /**
     * 获取全部
     */
    @GetMapping("/")
    public List<Item> getAll() {
        return itemService.getAll();
    }

    /**
     * 根据关键词搜索某部门下的文章
     *
     * @param dep ,title
     */
    @GetMapping("/search/{dep}/{title}")
    public List<Item> searchByDepAndTitle(@PathVariable("dep") String dep, @PathVariable("title") String title) {
        return itemService.searchByDepAndTitle(dep, title);

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
    @GetMapping("/search/{author}/{platform}/{startTime}/{endTime}")
    public List<Item> searchByAuthorAndPlateformAndPublishTime(@PathVariable("author") String author, @PathVariable("platform") String platform, @PathVariable("startTime") String startTime, @PathVariable("endTime") String endTime) throws ParseException {

        return itemService.searchByAuthorAndPlateformAndPublishTime(author, platform, startTime, endTime);
    }


    /**
     * delete by query 根据用户姓名删除数据
     *
     * @param name
     */
    @DeleteMapping("/name/{name}")
    public void deleteByAuthor(@PathVariable("name") String name) {
        itemService.deleteByAuthor(name);
    }
}
