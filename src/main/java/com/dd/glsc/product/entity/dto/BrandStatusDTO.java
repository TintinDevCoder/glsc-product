package com.dd.glsc.product.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dd.common.valid.ListValue;
import com.dd.common.valid.NotOnlyBlank;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import com.dd.common.valid.group.UpdateStatus;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Data
public class BrandStatusDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
    @Null(message = "新增不能指定品牌id", groups = {AddGroup.class})
    @NotNull(message = "修改品牌id必须有值", groups = {UpdateGroup.class})
	private Long brandId;

	/**
	 * 显示状态[0-不显示；1-显示]
	 */
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatus.class})
	private Integer showStatus;
}
