package com.dd.glsc.product.entity.dto;

import com.dd.common.valid.ListValue;
import com.dd.common.valid.NotOnlyBlank;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import com.dd.common.valid.group.UpdateStatus;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 商品属性
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Data
public class AttrAddAndUpdateDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 属性id
	 */
    @Null(message = "新增不能指定属性id", groups = {AddGroup.class})
    @NotNull(message = "修改属性id必须有值", groups = {UpdateGroup.class})
	private Long attrId;
	/**
	 * 属性名
	 */
    @NotBlank(message = "品牌名不能为空", groups = {AddGroup.class})
    @NotOnlyBlank(groups = {AddGroup.class, UpdateGroup.class})
	private String attrName;
	/**
	 * 是否需要检索[0-不需要，1-需要]
	 */
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatus.class})
    @NotNull(message = "检索不能为空", groups = {AddGroup.class})
	private Integer searchType;
	/**
	 * 值类型[0-为单个值，1-可以选择多个值]
	 */
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatus.class})
    @NotNull(message = "值类型不能为空", groups = {AddGroup.class})
	private Integer valueType;
	/**
	 * 属性图标
	 */
    @URL(message = "属性图标必须是一个合法的URL地址", groups = {AddGroup.class, UpdateGroup.class})
    @NotBlank(message = "属性图标不能为空", groups = {AddGroup.class})
	private String icon;
	/**
	 * 可选值列表[用逗号分隔]
	 */
	private String valueSelect;
	/**
	 * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
	 */
    @ListValue(vals = {0,1,2}, groups = {AddGroup.class, UpdateStatus.class})
	private Integer attrType;
	/**
	 * 启用状态[0 - 禁用，1 - 启用]
	 */
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatus.class})
	private Integer enable;
	/**
	 * 所属分类
	 */
    @NotNull(message = "分类不能为空", groups = {AddGroup.class})
	private Long catelogId;
	/**
	 * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
	 */
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatus.class})
	private Integer showDesc;
    /**
     * 属性分组Id
     */
    private Long attrGroupId;
}
