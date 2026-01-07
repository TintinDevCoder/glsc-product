package com.dd.glsc.product.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dd.common.common.BusinessException;
import com.dd.common.common.ErrorCode;
import com.dd.glsc.product.entity.BrandEntity;
import com.dd.glsc.product.entity.CategoryEntity;
import com.dd.glsc.product.service.BrandService;
import com.dd.glsc.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.Query;

import com.dd.glsc.product.dao.CategoryBrandRelationDao;
import com.dd.glsc.product.entity.CategoryBrandRelationEntity;
import com.dd.glsc.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String brandId = (String) params.get("brandId");
        QueryWrapper<CategoryBrandRelationEntity> categoryBrandRelationEntityQueryWrapper = new QueryWrapper<>();
        if (!StrUtil.isEmpty(brandId)) {
            categoryBrandRelationEntityQueryWrapper.lambda().eq(CategoryBrandRelationEntity::getBrandId, brandId);
        }
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                categoryBrandRelationEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void saveCategoryBrandRelation(CategoryBrandRelationEntity categoryBrandRelation) {
        // 获取品牌id和分类id
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        if (brandId == null || catelogId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "品牌或分类id不能为空");
        }
        // 根据品牌id获取品牌实体
        BrandEntity brandEntity = brandService.getById(brandId);
        if (brandEntity == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "品牌不存在");
        }
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        if (categoryEntity == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类不存在");
        }
        // 设置品牌名称和分类名称
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        // 保存关联关系
        this.save(categoryBrandRelation);
    }

    @Override
    public List<CategoryBrandRelationEntity> getBrandsListByCatelogId(Long catId) {
        // 根据分类id获取品牌关联实体列表
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CategoryBrandRelationEntity::getCatelogId, catId);
        List<CategoryBrandRelationEntity> BrandEntities = this.list(queryWrapper);
        if (!CollUtil.isEmpty(BrandEntities)) {
            // 筛选启用的品牌
            // 获得品牌id列表
            List<Long> brandIds = BrandEntities.stream().map(entity -> entity.getBrandId()).collect(Collectors.toList());
            // 根据品牌id列表查询启用的品牌实体列表
            QueryWrapper<BrandEntity> brandQueryWrapper = new QueryWrapper<>();
            List<BrandEntity> brandEntities = new LinkedList<>();
            brandQueryWrapper.lambda().in(BrandEntity::getBrandId, brandIds).eq(BrandEntity::getShowStatus, 1);
            brandEntities = brandService.list(brandQueryWrapper);
            // 将品牌实体列表转换为品牌关联实体列表
            BrandEntities = brandEntities.stream().map(brandEntity -> {
                CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
                categoryBrandRelationEntity.setBrandId(brandEntity.getBrandId());
                categoryBrandRelationEntity.setBrandName(brandEntity.getName());
                return categoryBrandRelationEntity;
            }).collect(Collectors.toList());
        }
        return BrandEntities;
    }

    @Override
    public void removeByBrandIds(List<Long> list) {
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(CategoryBrandRelationEntity::getBrandId, list);
        this.remove(queryWrapper);
    }

}