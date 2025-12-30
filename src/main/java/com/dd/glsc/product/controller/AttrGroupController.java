package com.dd.glsc.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.dd.common.common.BaseResponse;
import com.dd.common.common.ResultUtils;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import com.dd.glsc.product.entity.dto.AttrGroupRelationDTO;
import com.dd.glsc.product.entity.vo.AttrGroupAttrVO;
import com.dd.glsc.product.service.AttrAttrgroupRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dd.glsc.product.entity.AttrGroupEntity;
import com.dd.glsc.product.service.AttrGroupService;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.R;


/**
 * 属性分组
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 11:37:43
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    // 分组与属性的操作

    /**
     * 属性分组对应属性列表
     */
    @RequestMapping("/{attrGroupId}/attr/relation")
    //@RequiresPermissions("product:attrgroup:list")
    public BaseResponse<List<AttrGroupAttrVO>> getAttrById(@PathVariable("attrGroupId") Long attrGroupId) {
        List<AttrGroupAttrVO> attrByAttrGroupId = attrGroupService.getAttrByAttrGroupId(attrGroupId);

        return ResultUtils.success(attrByAttrGroupId);
    }

    /**
     * 获取属性分组没有关联的属性
     * @param params
     * @param attrGroupId
     * @return
     */
    @RequestMapping("/{attrGroupId}/noattr/relation")
    //@RequiresPermissions("product:attrgroup:list")
    public BaseResponse<PageUtils> getAttrById(@RequestParam Map<String, Object> params, @PathVariable("attrGroupId") Long attrGroupId) {
        PageUtils page = attrGroupService.getNoAttrByAttrGroupId(params, attrGroupId);

        return ResultUtils.success(page);
    }

    /**
     * 删除分组和属性的关联
     */
    @RequestMapping("/attr/relation/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public BaseResponse relationRemove(@RequestBody List<AttrGroupRelationDTO> attrGroupRelationDTOList) {
        attrGroupService.relationRemove(attrGroupRelationDTOList);

        return ResultUtils.success();
    }

    @RequestMapping("/attr/relation")
    //@RequiresPermissions("product:attrgroup:list")
    public BaseResponse<List<AttrGroupAttrVO>> saveAttrRelation(@RequestBody List<AttrGroupRelationDTO> attrGroupRelationDTOList) {
        attrGroupService.saveAttrRelation(attrGroupRelationDTOList);

        return ResultUtils.success();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 通过categoryId获取列表
     */
    @RequestMapping("/list/{categoryId}")
    //@RequiresPermissions("product:attrgroup:list")
    public BaseResponse<PageUtils> listByCategoryId(@RequestParam Map<String, Object> params, @PathVariable("categoryId") Long categoryId) {
        PageUtils result = attrGroupService.queryPage(params, categoryId);
        return ResultUtils.success(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@Validated({AddGroup.class}) @RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @Transactional
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        // 删除所有属性与属性分组的关联关系
        attrAttrgroupRelationService.removeByAttrGroupIds(Arrays.asList(attrGroupIds));
        // 删除属性分组
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
