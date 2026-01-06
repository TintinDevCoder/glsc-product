package com.dd.glsc.product.entity.dto.SkuSave;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class SpuBoundsDTO {
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
}
