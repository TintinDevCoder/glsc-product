package com.dd.glsc.product.feign;

import com.dd.common.common.BaseResponse;
import com.dd.common.to.SkuReducationTO;
import com.dd.common.to.SpuBoundsTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 远程调用优惠券服务
 */
@FeignClient("glsc-coupon")
public interface CouponFeignService {
    /**
     * 保存spu积分信息
     * @param spuBoudsTO
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    BaseResponse saveSpuBounds(SpuBoundsTO spuBoudsTO);

    /**
     * 保存sku满减信息
     * @param skuReducationTO
     * @return
     */
    @PostMapping("/coupon/skufullreduction/saveSkuReduction")
    BaseResponse saveSkuReduction(SkuReducationTO skuReducationTO);

}
