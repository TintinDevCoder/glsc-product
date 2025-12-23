package com.dd.glsc.product.dao;

import com.dd.glsc.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
