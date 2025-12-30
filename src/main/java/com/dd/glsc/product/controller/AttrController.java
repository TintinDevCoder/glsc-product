package com.dd.glsc.product.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.dd.common.common.BaseResponse;
import com.dd.common.common.ResultUtils;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import com.dd.glsc.product.entity.AttrAttrgroupRelationEntity;
import com.dd.glsc.product.entity.dto.AttrAddAndUpdateDTO;
import com.dd.glsc.product.entity.vo.AttrAndAttrGroupVOAndUpdate;
import com.dd.glsc.product.service.AttrAttrgroupRelationService;
import com.dd.glsc.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dd.glsc.product.entity.AttrEntity;
import com.dd.glsc.product.service.AttrService;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.R;



/**
 * 商品属性
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 11:37:43
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 属性列表
     * @param params
     * @param catId
     * @param type
     * @return
     */
    @RequestMapping("/{type}/list/{catId}")
    //@RequiresPermissions("product:attr:list")
    public BaseResponse<PageUtils> list(@RequestParam Map<String, Object> params,
                                   @PathVariable("catId") Long catId,
                                   @PathVariable("type") String type){
        PageUtils page = attrService.queryPage(params, catId, type);

        return ResultUtils.success(page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public BaseResponse<AttrAndAttrGroupVOAndUpdate> info(@PathVariable("attrId") Long attrId){
        AttrAndAttrGroupVOAndUpdate attr = attrService.getByIdWithGroup(attrId);
        return ResultUtils.success(attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    @Transactional
    public BaseResponse save(@Validated(value = {AddGroup.class}) @RequestBody AttrAddAndUpdateDTO attrAddAndUpdateDTO){
        attrService.saveAttr(attrAddAndUpdateDTO);
        return ResultUtils.success();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public BaseResponse update(@Validated(value = {UpdateGroup.class}) @RequestBody AttrAddAndUpdateDTO attr){
		attrService.updateAttr(attr);

        return ResultUtils.success();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
