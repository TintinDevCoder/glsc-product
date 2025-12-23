package com.dd.glsc.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.common.utils.PageUtils;
import com.dd.glsc.product.entity.CategoryEntity;
import com.dd.glsc.product.entity.vo.CategoryVO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryVO> listWithTree();

    /**
     * 批量删除分类
     */
    void removeCategoryByIds(List<Long> asList);
}

