package com.dd.glsc.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.common.to.SkuInfoTO;
import com.dd.common.to.SkuTotalPriceTO;
import com.dd.common.utils.PageUtils;
import com.dd.glsc.product.entity.SkuImagesEntity;
import com.dd.glsc.product.entity.SkuInfoEntity;
import com.dd.glsc.product.entity.SkuSaleAttrValueEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 批量保存sku图片信息
     * @param skuimages
     */
    void saveSkuImagesBatch(List<SkuImagesEntity> skuimages);

    /**
     * 批量保存sku销售属性信息
     * @param attrs
     */
    void saveSkuSaleAttrValueBatch(List<SkuSaleAttrValueEntity> attrs);

    /**
     * 根据条件分页查询sku信息
     * @param params
     * @return
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 根据条件查询sku信息
     * @param params
     * @return
     */
    List<SkuInfoTO> queryByCondition(Map<String, Object> params);

    /**
     * 计算sku总价
     * @param skus
     * @return
     */
    BigDecimal getTotalPrice(List<SkuTotalPriceTO> skus);
}

