package com.dd.glsc.product.service.impl;

import com.dd.glsc.product.entity.vo.BrandEntityVO;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dd.common.utils.PageUtils;
import com.dd.common.utils.Query;

import com.dd.glsc.product.dao.BrandDao;
import com.dd.glsc.product.entity.BrandEntity;
import com.dd.glsc.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );
/*        IPage<BrandEntityVO> resultPage = page.convert(brandEntity -> {
            BrandEntityVO vo = new BrandEntityVO();
            vo.setBrandId(brandEntity.getBrandId());
            vo.setName(brandEntity.getName());
            vo.setLogo(brandEntity.getLogo());
            vo.setDescript(brandEntity.getDescript());
            vo.setShowStatus(brandEntity.getShowStatus() != null && brandEntity.getShowStatus() == 1);
            vo.setFirstLetter(brandEntity.getFirstLetter());
            vo.setSort(brandEntity.getSort());
            return vo;
        });*/
        return new PageUtils(page);
    }

}