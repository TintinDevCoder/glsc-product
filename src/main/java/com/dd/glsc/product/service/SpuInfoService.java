package com.dd.glsc.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.common.utils.PageUtils;
import com.dd.glsc.product.entity.SpuInfoEntity;
import com.dd.glsc.product.entity.dto.SkuSave.SpuSaveDTO;

import java.util.Map;

/**
 * spu信息
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 带条件分页查询
     * @param params
     * @return
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 保存spu信息
     * @param spuInfo
     */
    void saveSpuInfo(SpuSaveDTO spuInfo);

    /**
     * 获取sku编号的最后一段
     * @return
     */
    String getLastSegment(String str);
}

