package com.macro.mall.portal.component;

import com.macro.mall.portal.service.PmsSkuStockService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RabbitListener(queues = "mall.stock.cancel")
public class StockReserve {

    @Autowired
    private PmsSkuStockService pmsSkuStockService;

    @RabbitHandler
    public void handle(HashMap<String, Long> productId){
        //根据订单编号查询该订单的商品数量
        Integer productQuantity = pmsSkuStockService.getProductQuantity(productId);
        //根据商品id减卖家库存表的库存
        pmsSkuStockService.updateOrderStock(productQuantity,productId.get("productId"));
    }
}
