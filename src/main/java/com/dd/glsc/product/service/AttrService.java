package com.dd.glsc.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.common.utils.PageUtils;
import com.dd.glsc.product.entity.AttrEntity;
import com.dd.glsc.product.entity.dto.AttrAddAndUpdateDTO;
import com.dd.glsc.product.entity.vo.AttrAndAttrGroupVOAndUpdate;

import java.util.Map;

/**
 * 商品属性
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPage(Map<String, Object> params, Long catId, String type);

    /**
     * 根据属性id查询属性详情包括分组信息
     * @param attrId
     * @return
     */
    AttrAndAttrGroupVOAndUpdate getByIdWithGroup(Long attrId);

    /**
     * 修改属性信息
     * @param attr
     */
    void updateAttr(AttrAddAndUpdateDTO attr);

    /**
     * 保存属性信息
     * @param attrAddAndUpdateDTO
     */
    void saveAttr(AttrAddAndUpdateDTO attrAddAndUpdateDTO);
}

