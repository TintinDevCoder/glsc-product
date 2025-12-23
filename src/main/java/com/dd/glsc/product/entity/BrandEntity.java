package com.dd.glsc.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.ListValue;
import com.dd.common.valid.NotOnlyBlank;
import com.dd.common.valid.group.UpdateGroup;
import com.dd.common.valid.group.UpdateStatus;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
    @Null(message = "新增不能指定品牌id", groups = {AddGroup.class})
    @NotNull(message = "修改品牌id必须有值", groups = {UpdateGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
    @NotBlank(message = "品牌名不能为空", groups = {AddGroup.class})
    @NotOnlyBlank(groups = {AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
    @URL(message = "logo必须是一个合法的URL地址", groups = {AddGroup.class, UpdateGroup.class})
    @NotBlank(message = "logo不能为空", groups = {AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
    @NotEmpty(message = "介绍不能为空", groups = {AddGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatus.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGroup.class})
    @NotEmpty(message = "检索首字母不能为空", groups = {AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
    @Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
    @NotNull(message = "排序不能为空", groups = {AddGroup.class})
	private Integer sort;

    /**
     * 逻辑删除 0未删除 1已删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDelete;
}
