package com.dd.glsc.product.feign;

import com.dd.common.common.BaseResponse;
import com.dd.common.to.SkuReducationTO;
import com.dd.common.to.es.SkuEsModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("glsc-search")
public interface SearchFeignService {
    /**
     * es商品上架
     * @param skuEsModels
     * @return
     */
    @PostMapping("/search/save/product")
    BaseResponse productStatusUp(List<SkuEsModel> skuEsModels);
}
