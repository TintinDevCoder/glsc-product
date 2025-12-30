package com.dd.glsc.product.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.dd.glsc.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品属性
 * 
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
    @Select("SELECT DISTINCT catelog_id FROM pms_attr ${ew.customSqlSegment}")
    List<Long> countDistinctCatelogIds(@Param(Constants.WRAPPER) QueryWrapper<AttrEntity> queryWrapper);
}
