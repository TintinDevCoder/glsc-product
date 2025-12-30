package com.dd.glsc.product.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dd.common.common.BaseResponse;
import com.dd.common.common.ResultUtils;
import com.dd.common.valid.group.AddGroup;
import com.dd.common.valid.group.UpdateGroup;
import com.dd.common.valid.group.UpdateStatus;
import com.dd.glsc.product.entity.CategoryBrandRelationEntity;
import com.dd.glsc.product.entity.dto.BrandStatusDTO;
import com.dd.glsc.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dd.glsc.product.entity.BrandEntity;
import com.dd.glsc.product.service.BrandService;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.R;


/**
 * 品牌
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 11:37:43
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public BaseResponse save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult bindingResult*/){
        // 验证品牌数据
/*        if (bindingResult.hasErrors()) {
            // 获取校验的错误信息
            Map<String, String> map = new HashMap();
            map.put("400", "提交的数据不合法");
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                // 获取到错误提示
                String message = fieldError.getDefaultMessage();
                // 获取到错误的属性名字
                String field = fieldError.getField();
                map.put(field, message);
            }
            return ResultUtils.error(map);
        }*/

		brandService.save(brand);

        return ResultUtils.success();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class})@RequestBody BrandEntity brand){
		brandService.updateById(brand);
        // 同步更新其他关联表中的数据
        if (!StrUtil.isEmpty(brand.getName())) {
            categoryBrandRelationService.update(new UpdateWrapper<CategoryBrandRelationEntity>().lambda()
                    .eq(CategoryBrandRelationEntity::getBrandId, brand.getBrandId())
                    .set(CategoryBrandRelationEntity::getBrandName, brand.getName())
            );
        }
        return R.ok();
    }

    /**
     * 仅修改状态
     * @param brandStatusDTO
     * @return
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated({UpdateStatus.class})@RequestBody BrandStatusDTO brandStatusDTO){
        BrandEntity brand = new BrandEntity();
        brand.setBrandId(brandStatusDTO.getBrandId());
        brand.setShowStatus(brandStatusDTO.getShowStatus());
        brandService.updateById(brand);

        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    @Transactional
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
        // 删除分类绑定的品牌关联
        categoryBrandRelationService.removeByBrandIds(Arrays.asList(brandIds));
        // 删除品牌
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
