package com.macro.mall.portal.service;


/**
 * 订单管理Service
 * Created by macro on 2018/10/11.
 */
public interface OmsOrderService {

    /**
     * 更新订单
     * @param outTradeNo
     * @param paymentStatus
     * @param trackingNo
     */
    int updateOrder(String outTradeNo, String paymentStatus, String trackingNo);

    /**
     * 发送订单结果
     * @param outTradeNo
     */
    void sendOrderResult(String outTradeNo);
}
