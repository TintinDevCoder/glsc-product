package com.dd.glsc.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.common.utils.PageUtils;
import com.dd.glsc.product.entity.BrandEntity;
import com.dd.glsc.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存品牌与分类的关联关系
     * @param categoryBrandRelation
     */
    void saveCategoryBrandRelation(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 某分类下的品牌列表
     * @param catId
     * @return
     */
    List<CategoryBrandRelationEntity> getBrandsListByCatelogId(Long catId);

    /**
     * 根据品牌ids删除关联关系
     * @param list
     */
    void removeByBrandIds(List<Long> list);
}

