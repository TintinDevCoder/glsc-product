package com.dd.glsc.product.service.impl;

import com.dd.glsc.product.entity.CategoryBrandRelationEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.Query;

import com.dd.glsc.product.dao.AttrAttrgroupRelationDao;
import com.dd.glsc.product.entity.AttrAttrgroupRelationEntity;
import com.dd.glsc.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void removeByAttrGroupIds(List<Long> list) {
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(AttrAttrgroupRelationEntity::getAttrGroupId, list);
        this.remove(queryWrapper);
    }

    @Override
    public void deleteRelationByAttrIds(List<Long> list) {
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(AttrAttrgroupRelationEntity::getAttrId, list);
        this.remove(queryWrapper);
    }

}