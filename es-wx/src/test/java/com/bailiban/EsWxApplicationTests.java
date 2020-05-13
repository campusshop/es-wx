package com.bailiban;

import com.bailiban.entity.Item;
import com.bailiban.entity.Wx;
import com.bailiban.repository.WxRepository;
import com.bailiban.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class EsWxApplicationTests {
    @Autowired
    private ItemService itemService;
    @Autowired
    private WxRepository wxRepository;

    /**
     * 导入mysql数据
     */
    @Test
    public void importAll() {
        List<Item> list = new ArrayList<>();
        List<Wx> all = wxRepository.findAll();

        for (Wx wx : all) {
            Item item = new Item();
            BeanUtils.copyProperties(wx, item);
            list.add(item);
        }
        System.out.println(list.size());
        itemService.putList(list);
    }

    /**
     * 删除索引库
     */
    @Test
    void deleteIndex() {
        itemService.deleteIndex("wx-index");
    }
    @Test
    void getAll(){
        List<Item> all = itemService.getAll();
        int size = all.size();
        System.out.println(size);
    }
}
