package com.dd.glsc.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.common.utils.PageUtils;
import com.dd.glsc.product.entity.AttrGroupEntity;
import com.dd.glsc.product.entity.dto.AttrGroupRelationDTO;
import com.dd.glsc.product.entity.vo.AttrGroupAttrVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long categoryId);

    /**
     * 查找属性分组对应的属性
     * @param attrGroupId
     * @return
     */
    List<AttrGroupAttrVO> getAttrByAttrGroupId(Long attrGroupId);

    /**
     * 删除分组和属性的关联
     * @param attrGroupIds
     */
    void relationRemove(List<AttrGroupRelationDTO> attrGroupIds);

    /**
     * 获取属性分组没有关联的属性
     * @param params
     * @param attrGroupId
     * @return
     */
    PageUtils getNoAttrByAttrGroupId(Map<String, Object> params, Long attrGroupId);

    /**
     * 保存属性分组和属性的关联关系
     * @param attrGroupRelationDTOList
     */
    void saveAttrRelation(List<AttrGroupRelationDTO> attrGroupRelationDTOList);
}

