package com.macro.mall.portal.component;

import com.macro.mall.model.PaymentInfo;
import com.macro.mall.portal.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;

/**
 * 支付的处理者
 */
@Component
@RabbitListener(queues = "mall.pay.cancel")
public class PayReceiver {

    @Autowired
    PaymentService paymentService;

    @RabbitHandler
    public void handle(HashMap mapMessage){
        try {
            String outTradeNo = mapMessage.get("out_trade_no").toString();
            int count = Integer.parseInt(mapMessage.get("count").toString());
            // 如果没有支付成功，再次发送延迟检查队列
            if (count > 0) {
                // 进行支付状态检查
                System.out.println("正在进行第" + (6 - count) + "支付结果次检查");
                //调用alipayClient接口，根据out_trade_no查询支付信息
                PaymentInfo paymentInfo = paymentService.checkPaymentResult(outTradeNo);
                Thread thread = new Thread();
                thread.start();
                Thread.sleep(10000);
                //判断是否已经支付成功
                if (paymentInfo.getPaymentStatus()!=null&&(paymentInfo.getPaymentStatus().equals("TRADE_SUCCESS") || paymentInfo.getPaymentStatus().equals("TRADE_FINISHED"))) {
                    // 交易成功或者失败，记录交易状态
                    System.out.println("检查交易结果成功，记录交易状态。。。");// 修改支付的状态信息
                    // 修改支付信息
                    boolean b = paymentService.checkPaymentStatus(outTradeNo);
                    if(!b){
                        //修改为已支付
                        paymentService.updatePayment(paymentInfo.getCallbackContent(),outTradeNo,paymentInfo.getAlipayTradeNo());
                        // 发送系统消息，出发并发商品支付业务消息队列
                        paymentService.sendPaymentSuccess(paymentInfo.getOutTradeNo(),paymentInfo.getPaymentStatus(),paymentInfo.getAlipayTradeNo());
                    }
                } else {//未支付
                    // 再次进行延迟检查
                    System.out.println("正在进行第" + (6 - count) + "支付结果次检查，检查用户尚未付款成功，继续巡检");
                    paymentService.sendDelayPaymentCheck(outTradeNo, count - 1);
                }
            } else {
                System.out.println("支付结果次检查次数耗尽，支付未果。。。");
            }
        } catch (Exception e) {

        }
    }
}
