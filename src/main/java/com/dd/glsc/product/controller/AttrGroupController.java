package com.dd.glsc.product.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.dd.common.common.BaseResponse;
import com.dd.common.common.ResultUtils;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 通过categoryId获取列表
     */
    @RequestMapping("/list/{categoryId}")
    //@RequiresPermissions("product:attrgroup:list")
    public BaseResponse<PageUtils> listByCategoryId(@RequestParam Map<String, Object> params, @PathVariable("categoryId") Long categoryId){
        PageUtils result = attrGroupService.queryPage(params, categoryId);
        return ResultUtils.success(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@Validated({AddGroup.class}) @RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
