package com.macro.mall.portal.component;

import com.macro.mall.portal.service.OmsOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;

/**
 * 支付成功的处理者
 */
@Component
@RabbitListener(queues = "mall.paysuccess.cancel")
public class PaySuccessReceiver {

    @Autowired
    private OmsOrderService orderService;

    @RabbitHandler
    public void handle(HashMap mapMessage){
        String outTradeNo = mapMessage.get("outTradeNo").toString();
        String trackingNo = mapMessage.get("tradeNo").toString();
        String paymentStatus = mapMessage.get("paymentStatus").toString();
        // 消费代码
        orderService.updateOrder(outTradeNo,paymentStatus,trackingNo);
        // 发送一个订单成功的消息队列，由库存系统消费(或者调用库存接口)
        orderService.sendOrderResult(outTradeNo);
    }

}
