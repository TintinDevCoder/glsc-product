package com.dd.glsc.product.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.Query;

import com.dd.glsc.product.dao.AttrGroupDao;
import com.dd.glsc.product.entity.AttrGroupEntity;
import com.dd.glsc.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();
        if (!(categoryId == 0)) {
            attrGroupEntityQueryWrapper.lambda()
                    .eq(AttrGroupEntity::getCatelogId, categoryId);
        }
        String key = (String) params.get("key");
        if (key != null && !key.isEmpty()) {
            attrGroupEntityQueryWrapper.and(wrapper ->
                    wrapper.lambda().like(AttrGroupEntity::getAttrGroupId, key)
                            .or()
                            .like(AttrGroupEntity::getAttrGroupName, key)
            );
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                attrGroupEntityQueryWrapper
        );
        return new PageUtils(page);
    }

}