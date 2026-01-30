package com.dd.glsc.product.web;

import com.dd.glsc.product.entity.CategoryEntity;
import com.dd.glsc.product.entity.vo.Catelog2VO;
import com.dd.glsc.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("")
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/index","/",""})
    public String indexPage(Model model) {
        // 查询一级分类
        List<CategoryEntity> level1Categories = categoryService.getLevel1Categories();

        model.addAttribute("categorys", level1Categories);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2VO>> getCatelogJson() {
        Map<String, List<Catelog2VO>> map = categoryService.getCatelogJson();
        return map;
    }
}
