package com.dd.glsc.product.service.impl;

import com.dd.glsc.product.entity.SkuImagesEntity;
import com.dd.glsc.product.entity.SkuSaleAttrValueEntity;
import com.dd.glsc.product.service.SkuImagesService;
import com.dd.glsc.product.service.SkuSaleAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}