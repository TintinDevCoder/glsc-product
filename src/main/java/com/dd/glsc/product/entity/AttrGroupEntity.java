package com.dd.glsc.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.dd.common.valid.NotOnlyBlank;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 属性分组
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分组id
	 */
	@TableId
    @Null(message = "新增不能指定属性分组id", groups = {AddGroup.class})
    @NotNull(message = "修改属性分组id必须有值", groups = {UpdateGroup.class})
	private Long attrGroupId;
	/**
	 * 组名
	 */
    @NotBlank(message = "属性分组名不能为空", groups = {AddGroup.class})
    @NotOnlyBlank(groups = {AddGroup.class, UpdateGroup.class})
	private String attrGroupName;
	/**
	 * 排序
	 */
    @Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
    @NotNull(message = "排序不能为空", groups = {AddGroup.class})
	private Integer sort;
	/**
	 * 描述
	 */
    @NotEmpty(message = "介绍不能为空", groups = {AddGroup.class})
	private String descript;
	/**
	 * 组图标
	 */
    @URL(message = "图标必须是一个合法的URL地址", groups = {AddGroup.class, UpdateGroup.class})
    @NotBlank(message = "图标不能为空", groups = {AddGroup.class})
	private String icon;
	/**
	 * 所属分类id
	 */
    @NotNull(message = "分类不能为空", groups = {AddGroup.class})
	private Long catelogId;

}
