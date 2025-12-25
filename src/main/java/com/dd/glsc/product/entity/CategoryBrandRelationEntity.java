package com.dd.glsc.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.dd.common.valid.NotOnlyBlank;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * 品牌分类关联
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Data
@TableName("pms_category_brand_relation")
public class CategoryBrandRelationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
    @Null(message = "新增不能指定id", groups = {AddGroup.class})
    @NotNull(message = "修改id必须有值", groups = {UpdateGroup.class})
	private Long id;
	/**
	 * 品牌id
	 */
    @NotNull(message = "品牌id必须有值", groups = {AddGroup.class})
	private Long brandId;
	/**
	 * 分类id
	 */
    @NotNull(message = "分类id必须有值", groups = {AddGroup.class})
	private Long catelogId;
	/**
	 * 
	 */
	private String brandName;
	/**
	 * 
	 */
	private String catelogName;

}
