package com.macro.mall.portal.service;

import com.macro.mall.model.PaymentInfo;

public interface PaymentService {


    /**
     * 保存支付信息
     * @param paymentInfo
     */
    int save(PaymentInfo paymentInfo);

    /**
     *发送检查支付结果的消息队列
     * @param orderSn
     * @param i
     */
    void sendDelayPaymentCheck(String orderSn, int i);

    /**
     * 检查支付状态
     * @param outTradeNo
     * @return
     */
    boolean checkPaymentStatus(String outTradeNo);

    /**
     * 更新支付
     * @param callbackContent
     * @param outTradeNo
     * @param alipayTradeNo
     * @return
     */
    int updatePayment(String callbackContent, String outTradeNo, String alipayTradeNo);

    /**
     * 发送系统消息，出发并发商品支付业务消息队列
     * @param outTradeNo
     * @param paymentStatus
     * @param trade_no
     */
    void sendPaymentSuccess(String outTradeNo, String paymentStatus, String trade_no);

    /**
     * 进行支付状态检查
     * @param out_trade_no
     * @return
     */
    PaymentInfo checkPaymentResult(String out_trade_no);


}
