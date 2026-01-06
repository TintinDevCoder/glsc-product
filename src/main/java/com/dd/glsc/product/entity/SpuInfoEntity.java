package com.dd.glsc.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.dd.common.valid.ListValue;
import com.dd.common.valid.NotOnlyBlank;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import com.dd.common.valid.group.UpdateStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * spu信息
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Data
@TableName("pms_spu_info")
public class SpuInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId
    @Null(message = "新增不能指定id", groups = {AddGroup.class})
    @NotNull(message = "修改id必须有值", groups = {UpdateGroup.class})
	private Long id;

	/**
	 * 商品名称
	 */
    @NotBlank(message = "名称不能为空", groups = {AddGroup.class})
    @NotOnlyBlank(groups = {AddGroup.class, UpdateGroup.class}) //不能为全空格
	private String spuName;

	/**
	 * 商品描述
	 */
    @NotBlank(message = "描述不能为空", groups = {AddGroup.class})
    @NotOnlyBlank(groups = {AddGroup.class, UpdateGroup.class}) //不能为全空格
	private String spuDescription;

	/**
	 * 所属分类id
	 */
    @NotNull(message = "修改分类id必须有值", groups = {AddGroup.class})
	private Long catalogId;

	/**
	 * 品牌id
	 */
    @NotNull(message = "修改品牌id必须有值", groups = {AddGroup.class})
	private Long brandId;

	/**
	 * 商品重量
	 */
    @NotNull(message = "商品重量必须有值", groups = {AddGroup.class})
	private BigDecimal weight;

	/**
	 * 上架状态[0 - 下架，1 - 上架]
	 */
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatus.class})
	private Integer publishStatus;

	/**
	 * 
	 */
	private Date createTime;
	/**
	 * 
	 */
	private Date updateTime;

}
