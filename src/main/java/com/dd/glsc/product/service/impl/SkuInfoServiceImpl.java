package com.dd.glsc.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dd.glsc.product.entity.SkuImagesEntity;
import com.dd.glsc.product.entity.SkuSaleAttrValueEntity;
import com.dd.glsc.product.service.SkuImagesService;
import com.dd.glsc.product.service.SkuSaleAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.Query;

import com.dd.glsc.product.dao.SkuInfoDao;
import com.dd.glsc.product.entity.SkuInfoEntity;
import com.dd.glsc.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuImagesBatch(List<SkuImagesEntity> skuimages) {
        skuImagesService.saveBatch(skuimages);
    }

    @Override
    public void saveSkuSaleAttrValueBatch(List<SkuSaleAttrValueEntity> attrs) {
        skuSaleAttrValueService.saveBatch(attrs);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        // 模糊查询
        if (StrUtil.isNotEmpty(key)) {
            wrapper.and(w -> {
                w.lambda().eq(SkuInfoEntity::getSkuId, key).or().like(SkuInfoEntity::getSkuName, key);
            });
        }
        // 分类id查询
        if (StrUtil.isNotEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.lambda().eq(SkuInfoEntity::getCatalogId, Long.parseLong(catelogId));
        }
        // 品牌id查询
        if (StrUtil.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.lambda().eq(SkuInfoEntity::getBrandId, Long.parseLong(brandId));
        }
        // 价格区间查询
        if (StrUtil.isNotEmpty(min)) {
            wrapper.lambda().ge(SkuInfoEntity::getPrice, new BigDecimal(min));
        }else min = "0";
        if (StrUtil.isNotEmpty(max)) {
            try {
                BigDecimal maxPrice = new BigDecimal(max);
                // 保证输入的价格大于0并且大于等于最小价格
                if (maxPrice.compareTo(BigDecimal.ZERO) > 0 && maxPrice.compareTo(new BigDecimal(min)) >= 0) {
                    wrapper.lambda().le(SkuInfoEntity::getPrice, maxPrice);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}