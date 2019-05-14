package com.macro.mall.portal.service;


import com.macro.mall.model.PmsProduct;
import java.util.List;

/**
 * 商品管理Service
 * Created by macro on 2018/4/26.
 */
public interface PmsProductService {

    /**
     * 根据商品分类ID获取spu商品分页信息
     * @param categoryId
     * @return
     */
    List<PmsProduct> getProductByCategoryId(Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 根据分类id和商品id查询商品详情
     * @param categoryId
     * @param productId
     * @return
     */
    PmsProduct getProductDetail(Long categoryId, Long productId);
}
