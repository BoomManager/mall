package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.OmsOrderItemExample;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsSkuStockExample;
import com.macro.mall.portal.service.PmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 商品sku库存管理Service实现类
 * Created by macro on 2018/4/27.
 */
@Service
public class PmsSkuStockServiceImpl implements PmsSkuStockService {
    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;
    /**
     * 更新订单库存
     * @param
     * @return
     */
    @Override
    public Integer updateOrderStock(Integer productQuantity,Long productId){
        PmsSkuStockExample pmsSkuStockExample = new PmsSkuStockExample();
        PmsSkuStockExample.Criteria criteria = pmsSkuStockExample.createCriteria();
        criteria.andProductIdEqualTo(productId);
        List<PmsSkuStock> pmsSkuStocks = skuStockMapper.selectByExample(pmsSkuStockExample);
        PmsSkuStock pmsSkuStock = pmsSkuStocks.get(0);
        pmsSkuStock.setStock(pmsSkuStock.getStock()-productQuantity);//减库存
        return skuStockMapper.updateByPrimaryKey(pmsSkuStock);
    }

    /**
     * 获取订单商品数量
     * @param productId
     * @return
     */
    @Override
    public Integer getProductQuantity(HashMap<String, Long> productId) {
        OmsOrderItemExample omsOrderItemExample = new OmsOrderItemExample();
        OmsOrderItemExample.Criteria criteria = omsOrderItemExample.createCriteria();
        criteria.andProductIdEqualTo(productId.get("productId"));
        List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.selectByExample(omsOrderItemExample);
        OmsOrderItem omsOrderItem = omsOrderItems.get(0);
        return omsOrderItem.getProductQuantity();
    }

}
