package com.dd.glsc.product.entity.dto.SkuSave;

import com.dd.common.valid.group.AddGroup;
import com.dd.glsc.product.entity.SpuInfoEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SpuSaveDTO extends SpuInfoEntity {
    // spu描述图片
    @NotNull(message = "描述图片不能为空", groups = {AddGroup.class})
    private List<String> decript;
    // spu图片
    @NotNull(message = "图片不能为空", groups = {AddGroup.class})
    private List<String> images;
    // 积分信息
    private SpuBoundsDTO bounds;
    // 销售属性
    private List<BaseAttrs> baseAttrs;
    // sku信息
    private List<SkuDTO> skus;
}
