package com.dd.glsc.product.service.impl;

import com.dd.common.common.BusinessException;
import com.dd.common.common.ErrorCode;
import com.dd.glsc.product.entity.vo.CategoryVO;
import com.dd.glsc.product.entity.vo.Catelog2VO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.Query;

import com.dd.glsc.product.dao.CategoryDao;
import com.dd.glsc.product.entity.CategoryEntity;
import com.dd.glsc.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public List<Long> findCategoryPath(Long catId3) {
        List<Long> result = new LinkedList<>();
        CategoryEntity category3 = this.getById(catId3);
        if (category3 == null || category3.getCatLevel() == null || category3.getCatLevel() != 3) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        result.add(catId3);
        CategoryEntity category2 = this.getById(category3.getParentCid());
        if (category2 == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        result.add(category2.getCatId());
        CategoryEntity category1 = this.getById(category2.getParentCid());
        if (category1 == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        result.add(category1.getCatId());
        Collections.reverse(result);
        return result;
    }

    /**
     * 查询所有一级分类
     *
     * @return
     */
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CategoryEntity::getCatLevel, 1);
        List<CategoryEntity> categoryEntities = this.list(queryWrapper);
        return categoryEntities;
    }

    /**
     * 获取分类数据，封装成指定格式的JSON（后端自处理，20ms左右）
     * @return
     */
    @Override
    public Map<String, List<Catelog2VO>> getCatelogJson() {
        List<CategoryEntity> allCatelog = this.list();
        List<CategoryEntity> level1Catelog = allCatelog.stream()
                .filter(categoryEntity -> categoryEntity.getCatLevel() == 1)
                .collect(Collectors.toList());
        // 封装数据
        Map<String, List<Catelog2VO>> catalogJson = level1Catelog.stream().collect(
                Collectors.toMap(
                k->k.getCatId().toString(),
                v->{
            // 1级分类id
            Long catId = v.getCatId();
            // 2级分类
            List<CategoryEntity> level2Catelog = allCatelog.stream()
                    .filter(categoryEntity1 -> categoryEntity1.getParentCid().equals(catId))
                    .collect(Collectors.toList());
            // 封装2级分类
            List<Catelog2VO> collect = level2Catelog.stream().map(level2 -> {
                Catelog2VO catelog2VO = new Catelog2VO();
                catelog2VO.setId(level2.getCatId());
                catelog2VO.setName(level2.getName());
                catelog2VO.setCatalog1Id(catId);

                Long catId2 = level2.getCatId();
                // 3级分类
                List<CategoryEntity> level3Catelog = allCatelog.stream()
                        .filter(categoryEntity2 -> categoryEntity2.getParentCid().equals(catId2))
                        .collect(Collectors.toList());
                // 封装3级分类
                List<Catelog2VO.Catelog3VO> catelog3VOList = level3Catelog.stream().map(level3 -> {
                    Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO();
                    catelog3VO.setId(level3.getCatId());
                    catelog3VO.setName(level3.getName());
                    catelog3VO.setCatalog2Id(catId2);
                    return catelog3VO;
                }).collect(Collectors.toList());
                catelog2VO.setCatalog3List(catelog3VOList);
                return catelog2VO;
            }).collect(Collectors.toList());
            return collect;
        }));
        return catalogJson;
    }

    /**
     * 150ms 内完成（首次1.3s）
     * @return
     */
    public Map<String, List<Catelog2VO>> getCatelogJson2() {
        List<CategoryEntity> level1Categories = this.getLevel1Categories();
        Map<String, List<Catelog2VO>> result = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> level2 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().lambda().eq(CategoryEntity::getParentCid, v.getCatId()));
            if (level2 == null || level2.size() == 0) {
                return new ArrayList<>();
            }
            List<Catelog2VO> relevel2 = level2.stream().map(l2 -> {
                Catelog2VO catelog2VO = new Catelog2VO();
                catelog2VO.setId(l2.getCatId());
                catelog2VO.setName(l2.getName());
                catelog2VO.setCatalog1Id(v.getCatId());
                List<CategoryEntity> level3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().lambda().eq(CategoryEntity::getParentCid, l2.getCatId()));
                if (level3 == null || level3.size() == 0) {
                    return catelog2VO;
                }
                List<Catelog2VO.Catelog3VO> catelog3VOS = level3.stream().map(l3 -> {
                    Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO();
                    catelog3VO.setId(l3.getCatId());
                    catelog3VO.setName(l3.getName());
                    catelog3VO.setCatalog2Id(l2.getCatId());
                    return catelog3VO;
                }).collect(Collectors.toList());
                catelog2VO.setCatalog3List(catelog3VOS);
                return catelog2VO;
            }).collect(Collectors.toList());
            return relevel2;
        }));
        return result;
    }
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出所有的分类以及子分类，以树形结构组装起来
     * @return
     */
    @Override
    public List<CategoryVO> listWithTree() {
        //1. 查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2. 组装成父子的树形结构
        List<CategoryEntity> source = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .collect(Collectors.toList());
        //封装返回值
        List<CategoryVO> result = source.stream().map(menu -> {
            CategoryVO categoryVO = new CategoryVO();
            BeanUtils.copyProperties(menu, categoryVO);
            categoryVO.setChildren(getChildrens(categoryVO, entities)); //递归查找子分类
            return categoryVO;
        }).collect(Collectors.toList());
        sortList(result); //排序
        //3. 返回结果
        return result;
    }

    /**
     * 对节点排序
     * @param list
     */
    public void sortList(List<CategoryVO> list) {
        // 收集到新的列表中
        List<CategoryVO> sortedList = list.stream()
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList()); // 生成新的排序列表

        // 清空原始列表并将排序后的元素添加到原列表中
        list.clear();
        list.addAll(sortedList);

        // 递归排序子节点
        for (CategoryVO categoryVO : list) {
            if (categoryVO.getChildren() != null && categoryVO.getChildren().size() > 0) {
                sortList(categoryVO.getChildren());
            }
        }
    }

    /**
     * 批量删除分类
     */
    @Override
    public void removeCategoryByIds(List<Long> asList) {
        //检查当前删除的菜单，是否被别的地方引用
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(CategoryEntity::getParentCid, asList);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("当前删除的分类被引用，不能删除");
        }
        //逻辑删除
        baseMapper.deleteByIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        // 递归查找完整路径
        findParentPath(catelogId, path);

        // 反转链表
        Collections.reverse(path);
        return path.toArray(new Long[0]);
    }


    private void findParentPath(Long catelogId, List<Long> path) {
        CategoryEntity categoryEntity = this.getById(catelogId);
        path.add(categoryEntity.getCatId());
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), path);
        }
    }

    /**
     * 递归查找子分类
     * @param root
     * @param all
     * @return
     */
    private List<CategoryVO> getChildrens(CategoryVO root, List<CategoryEntity> all) {
        List<CategoryVO> children = new LinkedList<>();
        for (CategoryEntity entity : all) {
            if (entity.getParentCid().longValue() == root.getCatId().longValue()) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(entity, categoryVO);
                categoryVO.setChildren(getChildrens(categoryVO, all));
                children.add(categoryVO);
            }
        }
        return children;
    }
}