package com.dd.glsc.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dd.common.utils.PageUtils;
import com.dd.glsc.product.entity.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author dd
 * @email 18211882344@163.com
 * @date 2025-12-05 10:16:51
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

