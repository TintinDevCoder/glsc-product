package com.dd.glsc.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dd.common.common.ErrorCode;
import com.dd.common.common.ThrowUtils;
import com.dd.common.constant.ProductConstant;
import com.dd.glsc.product.entity.AttrAttrgroupRelationEntity;
import com.dd.glsc.product.entity.AttrGroupEntity;
import com.dd.glsc.product.entity.CategoryEntity;
import com.dd.glsc.product.entity.dto.AttrAddAndUpdateDTO;
import com.dd.glsc.product.entity.vo.AttrAndAttrGroupVOAndUpdate;
import com.dd.glsc.product.service.AttrAttrgroupRelationService;
import com.dd.glsc.product.service.AttrGroupService;
import com.dd.glsc.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.Query;

import com.dd.glsc.product.dao.AttrDao;
import com.dd.glsc.product.entity.AttrEntity;
import com.dd.glsc.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId, String type) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        if (catId != 0) {
            attrEntityQueryWrapper.lambda().eq(AttrEntity::getCatelogId, catId);
        }
        attrEntityQueryWrapper.lambda().and((attrEntityLambdaQueryWrapper -> {
            attrEntityLambdaQueryWrapper.eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ALL_TYPE.getCode());
            if (!StrUtil.isEmpty(type)) {
                if (type.equalsIgnoreCase(ProductConstant.ATTR_TYPE_BASE)) {
                    attrEntityLambdaQueryWrapper.or().eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
                } else if (type.equalsIgnoreCase(ProductConstant.ATTR_TYPE_SALE)) {
                    attrEntityLambdaQueryWrapper.or().eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_SALE);
                }
            }
        }));
        String key = (String) params.get("key");
        if (!StrUtil.isEmpty(key)) {
            attrEntityQueryWrapper.lambda().and(wrapper ->
                    wrapper.eq(AttrEntity::getAttrId, key)
                            .or().like(AttrEntity::getAttrName, key)
            );
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );
        List<AttrAndAttrGroupVOAndUpdate> collect = page.getRecords().stream().map((attrEntity -> {
            AttrAndAttrGroupVOAndUpdate attrAndAttrGroupVO = new AttrAndAttrGroupVOAndUpdate();
            // 复制属性信息
            BeanUtils.copyProperties(attrEntity, attrAndAttrGroupVO);

            // 获取分类信息
            Long catelogId = attrEntity.getCatelogId();
            CategoryEntity category = categoryService.getById(catelogId);
            if (category != null) {
                attrAndAttrGroupVO.setCatelogName(category.getName());
            }

            // 获取属性分组信息
            if (attrEntity.getAttrId() != null && (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() || attrEntity.getAttrType() == ProductConstant.AttrEnum.ALL_TYPE.getCode())) {
                // 联表信息
                QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(queryWrapper);
                // 属性分组信息
                if (attrAttrgroupRelationEntity != null) {
                    Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                    AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
                    if (attrGroup != null) {
                        attrAndAttrGroupVO.setAttrGroupName(attrGroup.getAttrGroupName());
                    }
                }
            }
            return attrAndAttrGroupVO;
        })).collect(Collectors.toList());
        // 返回 PageUtils 对象
        Page<AttrAndAttrGroupVOAndUpdate> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(collect);
        return new PageUtils(resultPage);
    }

    @Override
    public AttrAndAttrGroupVOAndUpdate getByIdWithGroup(Long attrId) {
        ThrowUtils.throwIf(attrId == null, ErrorCode.PARAMS_ERROR);
        AttrAndAttrGroupVOAndUpdate attrAndAttrGroupVO = new AttrAndAttrGroupVOAndUpdate();
        // 先查询属性信息
        AttrEntity attr = this.getById(attrId);
        ThrowUtils.throwIf(attr == null, ErrorCode.NOT_FOUND_ERROR);
        BeanUtils.copyProperties(attr, attrAndAttrGroupVO);

        // 查询分类信息
        if (attr.getCatelogId() != null) {
            CategoryEntity category = categoryService.getById(attr.getCatelogId());
            ThrowUtils.throwIf(category == null, ErrorCode.NOT_FOUND_ERROR);
            attrAndAttrGroupVO.setCatelogName(category.getName());
            // 查询分类完整路径
            List<Long> categoryPath = categoryService.findCategoryPath(attr.getCatelogId());
            attrAndAttrGroupVO.setCatelogPath(categoryPath);
        }

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() || attr.getAttrType() == ProductConstant.AttrEnum.ALL_TYPE.getCode()) {
            // 基本属性才有分组信息
            // 查询属性分组信息
            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(AttrAttrgroupRelationEntity::getAttrId, attrId);
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(queryWrapper);
            if (attrAttrgroupRelationEntity != null) {
                Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                if (attrGroupId != null) {
                    AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
                    attrAndAttrGroupVO.setAttrGroupName(attrGroup.getAttrGroupName());
                    attrAndAttrGroupVO.setAttrGroupId(attrGroup.getAttrGroupId());
                }
            }
        }
        return attrAndAttrGroupVO;
    }

    @Override
    @Transactional
    public void updateAttr(AttrAddAndUpdateDTO attr) {
        // 修改属性表
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        // 更新属性与属性分组的关联关系
        Long attrGroupId = attr.getAttrGroupId();
        if (attrEntity.getAttrType() != null && (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() || attrEntity.getAttrType() == ProductConstant.AttrEnum.ALL_TYPE.getCode()) && attrGroupId != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attrGroupId);
            // 是新增关联关系还是修改关联关系
            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
            Long count = attrAttrgroupRelationService.count(queryWrapper);
            if (count > 0) {
                // 如果有则修改
                LambdaUpdateWrapper<AttrAttrgroupRelationEntity> wrapper = new UpdateWrapper<AttrAttrgroupRelationEntity>().lambda()
                        .eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
                attrAttrgroupRelationService.update(relationEntity, wrapper);
            } else {
                // 如果没有关联关系则新增
                attrAttrgroupRelationService.save(relationEntity);
            }
        }
    }

    @Override
    public void saveAttr(AttrAddAndUpdateDTO attrAddAndUpdateDTO) {
        AttrEntity attr = new AttrEntity();
        BeanUtils.copyProperties(attrAddAndUpdateDTO, attr);
        attr.setEnable(1);
        // 保存属性信息
        this.save(attr);
        // 如果是基本属性，还需要保存属性与属性分组的关联关系
        if (attr.getAttrType() != null && (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() || attr.getAttrType() == ProductConstant.AttrEnum.ALL_TYPE.getCode())) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrAddAndUpdateDTO.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }
    }

}