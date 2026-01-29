package com.dd.glsc.product.feign;

import com.dd.common.common.BaseResponse;
import com.dd.common.to.WareSkuTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
/**
 * 远程调用库存服务
 */
@FeignClient("glsc-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasStack")
    BaseResponse<List<WareSkuTO>> hasSkuStack(List<Long> skuId);
}
