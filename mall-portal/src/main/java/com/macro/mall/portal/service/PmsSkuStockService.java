package com.macro.mall.portal.service;

import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.PmsSkuStock;

import java.util.HashMap;
import java.util.List;

/**
 * sku商品库存管理Service
 * Created by macro on 2018/4/27.
 */
public interface PmsSkuStockService {

    /**
     * 减库存
     * @param omsOrder
     * @return
     */
    Integer updateOrderStock(Integer omsOrder,Long productId);

    /**
     * 获取订单商品数量
     * @param productId
     * @return
     */
    Integer getProductQuantity(HashMap<String, Long> productId);
}
