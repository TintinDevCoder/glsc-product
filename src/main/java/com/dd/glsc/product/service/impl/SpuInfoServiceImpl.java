package com.dd.glsc.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dd.common.common.BaseResponse;
import com.dd.common.to.SkuReducationTO;
import com.dd.common.to.SpuBoudsTO;
import com.dd.glsc.product.entity.*;
import com.dd.glsc.product.entity.dto.SkuSave.BaseAttrs;
import com.dd.glsc.product.entity.dto.SkuSave.SkuDTO;
import com.dd.glsc.product.entity.dto.SkuSave.SpuBoundsDTO;
import com.dd.glsc.product.entity.dto.SkuSave.SpuSaveDTO;
import com.dd.glsc.product.feign.CouponFeignService;
import com.dd.glsc.product.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.Query;

import com.dd.glsc.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    public PageUtils queryPageByCondition(Map<String, Object> params) {
        // 获取参数
        String statusK = (String) params.get("status");
        Integer status = StrUtil.isEmpty(statusK) ? null : Integer.parseInt(statusK);

        String keyK = (String) params.get("key");
        String key = StrUtil.isEmpty(keyK) ? null : keyK;

        String brandIdK = (String) params.get("brandId");
        Long brandId = StrUtil.isEmpty(brandIdK) ? null : Long.parseLong(brandIdK);

        String catelogIdK = (String) params.get("catelogId");
        Long catelogId = StrUtil.isEmpty(catelogIdK) ? null : Long.parseLong(catelogIdK);
        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<>();
        if (status != null && (status == 0 || status == 1 || status == 2)) {
            spuInfoEntityQueryWrapper.lambda().eq(SpuInfoEntity::getPublishStatus, status);
        }
        if (key != null && !key.isEmpty()) {
            spuInfoEntityQueryWrapper.lambda().and(wrapper ->
                    wrapper.eq(SpuInfoEntity::getId, key)
                            .or()
                            .like(SpuInfoEntity::getSpuName, key)
                            .or()
                            .like(SpuInfoEntity::getSpuDescription, key)
            );
        }
        if (brandId != null && brandId != 0) {
            spuInfoEntityQueryWrapper.lambda().eq(SpuInfoEntity::getBrandId, brandId);
        }
        if (catelogId != null && catelogId != 0) {
            spuInfoEntityQueryWrapper.lambda().eq(SpuInfoEntity::getCatalogId, catelogId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                spuInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }
    /**
     * 保存spu信息
     * @param spuInfo
     */
    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveDTO spuInfo) {
        // 1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        // 2、保存spu的描述图片 pms_spu_info_desc
        List<String> decripts = spuInfo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(String.join(",", decripts));
        spuInfoDescService.save(spuInfoDescEntity);

        // 3、保存spu的图片集 pms_spu_images
        List<String> images = spuInfo.getImages();
        if (images != null && images.size() > 0) {
            List<SpuImagesEntity> imagesEntities = images.stream().map(image -> {
                SpuImagesEntity imagesEntity = new SpuImagesEntity();
                imagesEntity.setSpuId(spuId);
                imagesEntity.setImgUrl(image);
                imagesEntity.setImgName(this.getLastSegment(image));
                return imagesEntity;
            }).collect(Collectors.toList());
            spuImagesService.saveBatch(imagesEntities);
        }

        // 4、保存spu的规格参数；pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        if (baseAttrs != null && baseAttrs.size() > 0) {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setSpuId(spuId);
                productAttrValueEntity.setAttrId(baseAttr.getAttrId());
                productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
                productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
                // 通过attrId查询attrName
                QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().select(AttrEntity::getAttrName).eq(AttrEntity::getAttrId, baseAttr.getAttrId());
                String name = attrService.getOne(queryWrapper).getAttrName();
                productAttrValueEntity.setAttrName(name);

                return productAttrValueEntity;
            }).collect(Collectors.toList());
            productAttrValueService.saveBatch(productAttrValueEntities);
        }

        // 5、保存spu对应的sku信息；pms_sku_info、pms_sku_images、pms_sku_sale_attr_value 以及 glsc_sms 中的相关表
        List<SkuDTO> skus = spuInfo.getSkus();
        if (skus != null || skus.size() > 0) {
            for (SkuDTO sku : skus) {
                // 5.1、保存sku的基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setSpuId(spuId);
                skuInfoEntity.setBrandId(spuInfo.getBrandId());
                skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
                // 设置sku的默认图片
                List<SkuImagesEntity> skuimages = sku.getImages();
                String defaultImg = "";
                for (SkuImagesEntity skuimage : skuimages) {
                    if (skuimage.getDefaultImg() == 1) {
                        defaultImg = skuimage.getImgUrl();
                        break;
                    }
                }
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                // 保存sku描述，多个用逗号分隔
                List<String> descars = sku.getDescar();
                skuInfoEntity.setSkuDesc(String.join(",", descars));
                // 保存
                skuInfoService.save(skuInfoEntity);

                // 获取保存后的skuId
                Long skuId = skuInfoEntity.getSkuId();
                // 5.2、保存sku的图片信息 pms_sku_images
                // 过滤掉没有图片地址的
                skuimages = skuimages.stream().filter(img -> img.getImgUrl() != null && !img.getImgUrl().isEmpty()).collect(Collectors.toList());
                // 设置skuId
                for (SkuImagesEntity skuimage : skuimages) {
                    skuimage.setSkuId(skuId);
                }
                // 批量保存
                if (skuimages != null && skuimages.size() > 0) {
                    skuInfoService.saveSkuImagesBatch(skuimages);
                }

                // 5.3、保存sku的销售属性信息 pms_sku_sale_attr_value
                List<SkuSaleAttrValueEntity> attrs = sku.getAttr();
                for (SkuSaleAttrValueEntity attr : attrs) {
                    attr.setSkuId(skuId);
                }
                // 批量保存
                if (attrs != null && attrs.size() > 0) {
                    skuInfoService.saveSkuSaleAttrValueBatch(attrs);
                }

                // 5.4、保存sku的优惠、满减等信息 glsc_sms -> sms_sku_ladder（数量打折）、sms_sku_full_reduction（满减）、sms_member_price(会员价格)
                // 远程调用优惠服务进行保存
                SkuReducationTO skuReducationTO = new SkuReducationTO();
                BeanUtils.copyProperties(sku, skuReducationTO);
                skuReducationTO.setSkuId(skuId);
                BaseResponse r = couponFeignService.saveSkuReduction(skuReducationTO);
                if (r.getCode() != 0) {
                    throw new RuntimeException("远程保存sku优惠信息失败");
                }
            }
        }

        // 6、保存spu的积分信息；glsc_sms -> sms_spu_bounds
        SpuBoundsDTO bounds = spuInfo.getBounds();
        SpuBoudsTO spuBoudsTO = new SpuBoudsTO();
        BeanUtils.copyProperties(bounds, spuBoudsTO);
        spuBoudsTO.setSpuId(spuId);
        // 远程调用积分服务保存
        BaseResponse r = couponFeignService.saveSpuBounds(spuBoudsTO);
        if (r.getCode() != 0) {
            throw new RuntimeException("远程保存spu积分信息失败");
        }
    }

    @Override
    public String getLastSegment(String str) {
        String[] segments = str.split("/");
        return segments.length > 0 ? segments[segments.length - 1] : str;
    }

}