package com.dd.glsc.product.entity.vo;

import com.dd.glsc.product.entity.dto.AttrAddAndUpdateDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品属性
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Data
public class AttrAndAttrGroupVOAndUpdate extends AttrAddAndUpdateDTO implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 属性分组名称
     */
    private String attrGroupName;

    /**
     * 所属分类名称
     */
    private String catelogName;

    /**
     * 所属分类完整路径
     */
    private List<Long> catelogPath;
}
