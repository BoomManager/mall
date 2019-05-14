package com.macro.mall.portal.service.impl;

import com.alipay.api.AlipayClient;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.OmsOrderItemExample;
import com.macro.mall.portal.domain.QueueEnum;
import com.macro.mall.portal.service.OmsOrderService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import java.util.HashMap;
import java.util.List;

@Service
public class OmsOrderServiceImpl implements OmsOrderService {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    AlipayClient alipayClient;

    /**
     *  更新订单状态.
     * @param outTradeNo
     * @param paymentStatus
     * @param trackingNo
     */
    @Override
    public int updateOrder(String outTradeNo, String paymentStatus, String trackingNo) {
        OmsOrderExample omsOrderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = omsOrderExample.createCriteria();
        criteria.andOrderSnEqualTo(outTradeNo);
        List<OmsOrder> omsOrders = orderMapper.selectByExample(omsOrderExample);
        OmsOrder omsOrder = omsOrders.get(0);
        omsOrder.setStatus(1);//修改支付状态为待发货
        omsOrder.setPayType(1);//修改支付类型为支付宝
        omsOrder.setSourceType(0);//修改来源为pc端
        omsOrder.setPaymentStatus(paymentStatus);//和阿里对接的订单支付状态
        omsOrder.setAlipayTradeNo(trackingNo);//阿里交易码
        return orderMapper.updateByPrimaryKey(omsOrder);
    }

    /**
     * 发送一个订单成功的消息队列，由库存系统消费(或者调用库存接口)
     * @param outTradeNo
     */
    @Override
    public void sendOrderResult(String outTradeNo) {
        OmsOrderItemExample omsOrderItemExample = new OmsOrderItemExample();
        OmsOrderItemExample.Criteria criteria = omsOrderItemExample.createCriteria();
        criteria.andOrderSnEqualTo(outTradeNo);
        List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.selectByExample(omsOrderItemExample);
        OmsOrderItem omsOrder = omsOrderItems.get(0);
        Long productId = omsOrder.getProductId();
        HashMap<String, Long> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("productId",productId);
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_STOCK_QUEUE.getExchange(), QueueEnum.QUEUE_STOCK_QUEUE.getRouteKey(),objectObjectHashMap, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //给消息设置延迟毫秒值
                message.getMessageProperties().setExpiration(String.valueOf(0));
                return message;
            }
        });
    }
}
