package com.dd.glsc.product.entity.dto.SkuSave;

import com.dd.glsc.product.entity.SkuImagesEntity;
import com.dd.glsc.product.entity.SkuInfoEntity;
import com.dd.glsc.product.entity.SkuSaleAttrValueEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class SkuDTO extends SkuInfoEntity {
    // sku销售属性
    private List<SkuSaleAttrValueEntity> attr;
    // sku图片
    private List<SkuImagesEntity> images;
    // sku优惠信息
    private List<String> descar;

    // sku满减信息
    private Integer fullCount;
    // sku折扣
    private BigDecimal discount;
    // 是否参与其他优惠
    private Integer countStatus;

    // sku满减价格
    private BigDecimal fullPrice;
    // sku减免价格
    private BigDecimal reducePrice;
    // 是否参与其他优惠
    private Integer priceStatus;

    private List<MemberPrice> memberPrice;
}
