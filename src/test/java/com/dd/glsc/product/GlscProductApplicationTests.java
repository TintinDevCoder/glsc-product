package com.dd.glsc.product;


import com.dd.glsc.product.dao.AttrDao;
import com.dd.glsc.product.entity.vo.AttrAndAttrGroupVOAndUpdate;
import com.dd.glsc.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GlscProductApplicationTests {
    @Autowired
    CategoryService categoryService;
    @Autowired
    AttrDao attrDao;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set("hello", "world" + UUID.randomUUID().toString());
        String hello = stringRedisTemplate.opsForValue().get("hello");
        System.out.println("hello = " + hello);
    }
}