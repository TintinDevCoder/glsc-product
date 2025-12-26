package com.dd.glsc.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dd.common.common.BaseResponse;
import com.dd.common.common.ResultUtils;
import com.dd.glsc.product.entity.CategoryBrandRelationEntity;
import com.dd.glsc.product.entity.vo.CategoryVO;
import com.dd.glsc.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.dd.glsc.product.entity.CategoryEntity;
import com.dd.glsc.product.service.CategoryService;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.R;



/**
 * 商品三级分类
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 11:37:43
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @GetMapping("/path/{catId}")
    public BaseResponse<List<Long>> getCategoryPath(@PathVariable("catId") Long catId) {
        List<Long> categoryPath = categoryService.findCategoryPath(catId);
        return ResultUtils.success(categoryPath);
    }

    /**
     * 查出所有的分类以及子分类，以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    //@RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        List<CategoryVO> entityList = categoryService.listWithTree();
        return R.ok().put("page", entityList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/sort")
    //@RequiresPermissions("product:category:update")
    public R updateSortBatch(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        // 同步更新其他关联表中的数据
        if (!StrUtil.isEmpty(category.getName())) {
            categoryBrandRelationService.update(new UpdateWrapper<CategoryBrandRelationEntity>().lambda()
                    .eq(CategoryBrandRelationEntity::getCatelogId, category.getCatId())
                    .set(CategoryBrandRelationEntity::getCatelogName, category.getName())
            );
        }
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		//categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeCategoryByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
