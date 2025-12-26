package com.dd.glsc.product;


import com.dd.glsc.product.dao.AttrDao;
import com.dd.glsc.product.entity.vo.AttrAndAttrGroupVOAndUpdate;
import com.dd.glsc.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GlscProductApplicationTests {
    @Autowired
    CategoryService categoryService;
    @Autowired
    AttrDao attrDao;
    @Test
    void contextLoads() {

    }
}