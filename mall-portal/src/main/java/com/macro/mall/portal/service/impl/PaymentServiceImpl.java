package com.macro.mall.portal.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.macro.mall.mapper.OmsOrderSettingMapper;
import com.macro.mall.mapper.PaymentInfoMapper;
import com.macro.mall.model.OmsOrderSetting;
import com.macro.mall.model.PaymentInfo;
import com.macro.mall.model.PaymentInfoExample;
import com.macro.mall.portal.domain.QueueEnum;
import com.macro.mall.portal.service.PaymentService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    AlipayClient alipayClient;

    /**
     * 保存支付信息
     * @param paymentInfo
     */
    @Override
    public int save(PaymentInfo paymentInfo) {
        return paymentInfoMapper.insert(paymentInfo);
    }

    /**
     * 发送检查支付结果的消息队列
     * @param orderSn
     * @param count
     */
    @Override
    public void sendDelayPaymentCheck(String orderSn, int count) {
        //获取订单超时时间
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("out_trade_no",orderSn);
        hashMap.put("count",count);

        //给延迟队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_PAY_CANCEL.getExchange(), QueueEnum.QUEUE_PAY_CANCEL.getRouteKey(), hashMap, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //给消息设置延迟毫秒值
                message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                return message;
            }
        });
    }

    /**
     * 检查支付状态
     * @param outTradeNo
     * @return
     */
    @Override
    public boolean checkPaymentStatus(String outTradeNo) {
        boolean b = false;
        PaymentInfoExample paymentInfoExample = new PaymentInfoExample();
        PaymentInfoExample.Criteria criteria = paymentInfoExample.createCriteria();
        criteria.andOutTradeNoEqualTo(outTradeNo);
        List<PaymentInfo> paymentInfos = paymentInfoMapper.selectByExample(paymentInfoExample);
        PaymentInfo paymentInfo = paymentInfos.get(0);
        if(paymentInfo.getPaymentStatus().equals("已支付")){
            b = true;
        }
        return b;
    }

    /**
     * 更新支付
     * @param callbackContent
     * @param outTradeNo
     * @param alipayTradeNo
     * @return
     */
    @Override
    public int updatePayment(String callbackContent, String outTradeNo, String alipayTradeNo) {
        //根据订单编号获取支付信息
        PaymentInfoExample paymentInfoExample = new PaymentInfoExample();
        PaymentInfoExample.Criteria criteria = paymentInfoExample.createCriteria();
        criteria.andOutTradeNoEqualTo(outTradeNo);
        List<PaymentInfo> paymentInfos = paymentInfoMapper.selectByExample(paymentInfoExample);
        PaymentInfo paymentInfoUpdate = paymentInfos.get(0);
        //更新支付信息
        paymentInfoUpdate.setPaymentStatus("已支付");
        paymentInfoUpdate.setCallbackContent(callbackContent);
        paymentInfoUpdate.setOutTradeNo(outTradeNo);
        paymentInfoUpdate.setAlipayTradeNo(alipayTradeNo);
        paymentInfoUpdate.setCallbackTime(new Date());
        paymentInfoUpdate.setConfirmTime(new Date());
        return paymentInfoMapper.updateByPrimaryKey(paymentInfoUpdate);
    }

    /**
     * 发送系统消息，出发并发商品支付业务消息队列
     * @param outTradeNo
     * @param paymentStatus
     * @param tradeNo
     */
    @Override
    public void sendPaymentSuccess(String outTradeNo, String paymentStatus, String tradeNo) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("outTradeNo",outTradeNo);
        hashMap.put("paymentStatus",paymentStatus);
        hashMap.put("tradeNo",tradeNo);
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_PAYMENT_SUCCESS_CANCEL.getExchange(), QueueEnum.QUEUE_PAYMENT_SUCCESS_CANCEL.getRouteKey(), hashMap, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //给消息设置延迟毫秒值
                message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                return message;
            }
        });
    }

    /**
     * 进行支付状态检查
     * @param outTradeNo
     * @return
     */
    @Override
    public PaymentInfo checkPaymentResult(String outTradeNo) {
        PaymentInfo paymentInfo = new PaymentInfo();
        // 调用alipayClient接口，根据out_trade_no查询支付状态
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String,Object> mapString = new HashMap<String,Object>();
        mapString.put("out_trade_no",outTradeNo);
        String s = JSON.toJSONString(mapString);
        request.setBizContent(s);
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        // 等待付款、付款成功、付款失败、已经结束
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setCallbackContent(response.getMsg());
        if(response.isSuccess()){
            System.out.println("交易已经创建");
            paymentInfo.setPaymentStatus(response.getTradeStatus());
            paymentInfo.setAlipayTradeNo(response.getTradeNo());
        } else {
            System.out.println("交易未创建");
        }
        return paymentInfo;
    }


}
