package com.dd.glsc.product;


import com.dd.glsc.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GlscProductApplicationTests {
    @Autowired
    CategoryService categoryService;

    @Test
    void contextLoads() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        System.out.println(Arrays.asList(catelogPath));
    }
}