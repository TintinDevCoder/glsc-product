package com.dd.glsc.product.service.impl;

import com.dd.glsc.product.entity.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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