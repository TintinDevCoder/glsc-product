package com.dd.glsc.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dd.common.common.BusinessException;
import com.dd.common.common.ErrorCode;
import com.dd.common.common.ThrowUtils;
import com.dd.common.constant.ProductConstant;
import com.dd.glsc.product.dao.AttrDao;
import com.dd.glsc.product.entity.AttrAttrgroupRelationEntity;
import com.dd.glsc.product.entity.AttrEntity;
import com.dd.glsc.product.entity.dto.AttrGroupRelationDTO;
import com.dd.glsc.product.entity.vo.AttrAndAttrGroupVOAndUpdate;
import com.dd.glsc.product.entity.vo.AttrGroupAttrVO;
import com.dd.glsc.product.entity.vo.AttrGroupWithAttrList;
import com.dd.glsc.product.service.AttrAttrgroupRelationService;
import com.dd.glsc.product.service.AttrService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrService attrService;

    @Autowired
    AttrDao attrDao;

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

    @Override
    public List<AttrGroupAttrVO> getAttrByAttrGroupId(Long attrGroupId) {
        // 构建查询条件
        QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<>();
        if (attrGroupId != null && attrGroupId != 0) {
            attrAttrgroupRelationEntityQueryWrapper.lambda().eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupId);
        }
        // 属性分组查询条件
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationService.getBaseMapper().selectList(attrAttrgroupRelationEntityQueryWrapper);
        List<Long> attrIds = attrAttrgroupRelationEntities.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        List<AttrGroupAttrVO> result = null;
        // 如果没有属性，直接返回空列表
        if (attrIds.size() == 0 || attrIds == null) {
            return result;
        }
        // 查询属性信息
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        attrEntityQueryWrapper.lambda().in(AttrEntity::getAttrId, attrIds);
        List<AttrEntity> attrEntities = attrService.getBaseMapper().selectList(attrEntityQueryWrapper);
        if (attrIds.size() != attrEntities.size()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 转换
        result = attrEntities.stream().map(attrEntity -> {
            AttrGroupAttrVO attrGroupAttrVO = new AttrGroupAttrVO();
            attrGroupAttrVO.setAttrId(attrEntity.getAttrId());
            attrGroupAttrVO.setAttrName(attrEntity.getAttrName());
            attrGroupAttrVO.setValueSelect(attrEntity.getValueSelect());
            return attrGroupAttrVO;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public void relationRemove(List<AttrGroupRelationDTO> attrGroupRelationDTOList) {
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        for (AttrGroupRelationDTO dto : attrGroupRelationDTOList) {
            // 直接在 lambda 内构建条件，不返回任何值
            queryWrapper.or(attrGroupEntityLambdaQueryWrapper ->
                    attrGroupEntityLambdaQueryWrapper
                            .eq(AttrAttrgroupRelationEntity::getAttrGroupId, dto.getAttrGroupId())
                            .eq(AttrAttrgroupRelationEntity::getAttrId, dto.getAttrId())
            );
        }
        attrAttrgroupRelationService.remove(queryWrapper);
    }

    /**
     * 获取属性分组没有关联的属性
     * @param params
     * @param attrGroupId
     * @return
     */
    @Override
    public PageUtils getNoAttrByAttrGroupId(Map<String, Object> params, Long attrGroupId) {
        // 当前分组只能关联自己所属分类里面的所有属性
        AttrGroupEntity attrGroupEntity = this.getById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        ThrowUtils.throwIf(catelogId == null, ErrorCode.PARAMS_ERROR, "属性分组所属分类不存在");
        // 当前分组只能关联别的分组没有引用的属性
        // 查出同分类下的其他分组
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();
        attrGroupEntityQueryWrapper.lambda().eq(AttrGroupEntity::getCatelogId, catelogId);
        List<AttrGroupEntity> attrGroupEntities = this.list(attrGroupEntityQueryWrapper);
        // 查出这些分组关联的属性
        // 得到属性分组id列表
        List<Long> groupIds = attrGroupEntities.stream()
                .map(AttrGroupEntity::getAttrGroupId)
                .collect(Collectors.toList());
        // 查询这些分组关联的属性
        QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<>();
        attrAttrgroupRelationEntityQueryWrapper.lambda().in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds);
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationService.list(attrAttrgroupRelationEntityQueryWrapper);
        // 得到属性id列表
        List<Long> attrIds = attrAttrgroupRelationEntities.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        // 从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        attrEntityQueryWrapper.lambda()
                .eq(AttrEntity::getCatelogId, catelogId)
                .and(attrEntityLambdaQueryWrapper -> {
                    attrEntityLambdaQueryWrapper.eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())
                            .or()
                    .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ALL_TYPE.getCode());
                });
        if (attrIds != null && attrIds.size() > 0) {
            attrEntityQueryWrapper.lambda().notIn(AttrEntity::getAttrId, attrIds);
        }
        String key = (String) params.get("key");
        if (key != null && !key.isEmpty()) {
            attrEntityQueryWrapper.and(wrapper ->
                    wrapper.lambda().like(AttrEntity::getAttrId, key)
                            .or()
                            .like(AttrEntity::getAttrName, key)
            );
        }
        IPage<AttrEntity> page = attrService.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 保存属性分组和属性的关联关系
     * @param attrGroupRelationDTOList
     */
    @Override
    public void saveAttrRelation(List<AttrGroupRelationDTO> attrGroupRelationDTOList) {
        // 验证属性是否分配给别的分组
        List<Long> attrIds = attrGroupRelationDTOList.stream().map(dto -> dto.getAttrId()).collect(Collectors.toList());
        List<Long> attrGroupIds = attrGroupRelationDTOList.stream().map(dto -> dto.getAttrGroupId()).collect(Collectors.toList());

        LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AttrAttrgroupRelationEntity::getAttrId, attrIds)
                .isNotNull(AttrAttrgroupRelationEntity::getAttrGroupId);
        long count = attrAttrgroupRelationService.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "部分属性已分配给其他分组，无法重复分配");
        // 验证属性和分组是否属于同一分类
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        //attrEntityQueryWrapper.select("DISTINCT catelog_id");
        attrEntityQueryWrapper.in("attr_id", attrIds);
        List<Long> ids1 = attrDao.countDistinctCatelogIds(attrEntityQueryWrapper);
        if (ids1.size() != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "属性和分组不属于同一分类，无法关联");
        }
        Long catelogId = ids1.get(0);

        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();
        attrGroupEntityQueryWrapper.in("attr_group_id", attrGroupIds);
        List<Long> ids2 = this.baseMapper.countDistinctCatelogIds(attrGroupEntityQueryWrapper);
        if (ids2.size() != 1 || !ids2.get(0).equals(catelogId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "属性和分组不属于同一分类，无法关联");
        }
        // 转换为实体类并保存
        List<AttrAttrgroupRelationEntity> relationEntities = attrGroupRelationDTOList.stream().map(dto -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(dto.getAttrGroupId());
            relationEntity.setAttrId(dto.getAttrId());
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationService.saveBatch(relationEntities);
    }

    @Override
    public List<AttrGroupWithAttrList> getAttrGroupWithAttr(Long catalogId) {
        List<AttrGroupWithAttrList> result = new LinkedList<>();
        // 获取到当前分类下的所有属性分组
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();
        attrGroupEntityQueryWrapper.lambda().eq(AttrGroupEntity::getCatelogId, catalogId);
        List<AttrGroupEntity> attrGroupEntities = this.list(attrGroupEntityQueryWrapper);
        List<Long> attrGroupIds = attrGroupEntities.stream().map(attrGroupEntity -> attrGroupEntity.getAttrGroupId()).collect(Collectors.toList());
        if (attrGroupEntities != null && attrGroupEntities.size() > 0) {
            // 获取这些分组下的所有属性关联关系
            QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<>();
            attrAttrgroupRelationEntityQueryWrapper.lambda().in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds);
            List<AttrAttrgroupRelationEntity> attrRelationList = attrAttrgroupRelationService.list(attrAttrgroupRelationEntityQueryWrapper);

            // 获取所有属性id,并获取属性信息
            List<Long> attrIds = attrRelationList.stream().map(attrRelationEntity -> attrRelationEntity.getAttrId()).collect(Collectors.toList());
            QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
            attrEntityQueryWrapper.lambda().in(AttrEntity::getAttrId, attrIds);
            List<AttrEntity> attrEntities = attrService.list(attrEntityQueryWrapper);

            // 将属性按分组进行分类
            Map<Long, List<Long>> attrGroupIdToAttrIds = new HashMap<>();
            for (AttrAttrgroupRelationEntity relation : attrRelationList) {
                Long groupId = relation.getAttrGroupId();
                Long attrId = relation.getAttrId();
                attrGroupIdToAttrIds.computeIfAbsent(groupId, k -> new LinkedList<>()).add(attrId);
            }
            // 查询属性信息并封装结果
            result = attrGroupEntities.stream().map(attrGroupEntity -> {
                AttrGroupWithAttrList vo = new AttrGroupWithAttrList();
                BeanUtils.copyProperties(attrGroupEntity, vo);
                List<Long> groupAttrIds = attrGroupIdToAttrIds.get(attrGroupEntity.getAttrGroupId());
                List<AttrEntity> groupAttrs = new LinkedList<>();
                if (groupAttrIds != null) {
                    groupAttrs = attrEntities.stream()
                            .filter(attrEntity -> groupAttrIds.contains(attrEntity.getAttrId()))
                            .collect(Collectors.toList());
                }
                vo.setAttrs(groupAttrs);
                return vo;
            }).collect(Collectors.toList());
            return result;
        }
        return result;
    }

}