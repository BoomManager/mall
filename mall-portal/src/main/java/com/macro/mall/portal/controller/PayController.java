package com.macro.mall.portal.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.PaymentInfo;
import com.macro.mall.portal.config.AlipayConfig;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付管理
 */
@Controller
@Api(tags = "PayController", description = "支付管理")
@RequestMapping("/member/pay")
public class PayController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OmsPortalOrderService omsPortalOrderService;
    @Autowired
    AlipayClient alipayClient;

    @ApiOperation("去支付")
    @RequestMapping(value = "/alipay/submit", method = RequestMethod.GET)
    @ResponseBody
    public String goToPay( String orderSn, BigDecimal totalAmount) {
        //
        //根据订单编号获取订单详情信息
        OmsOrderItem omsOrderItem =omsPortalOrderService.getOrderByOrderSn(orderSn);
        //获取商品名称
        String productName = omsOrderItem.getProductName();
        //创建PC场景下单并支付请求对象
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        //设置同步返回地址，HTTP/HTTPS开头字符串
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        //支付宝服务器主动通知商户服务器里指定的页面http/https路径。
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("out_trade_no",orderSn);//订单编号
        requestMap.put("product_code","FAST_INSTANT_TRADE_PAY");//产品交易码
        requestMap.put("total_amount","0.01");//实际付款金额
        requestMap.put("subject",productName);//商品名称
        //填充业务参数
        alipayRequest.setBizContent(JSON.toJSONString(requestMap));
        String form="";
        try {
            //调用SDK生成表单
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        // 生成(保存)支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderSn);
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setOrderId(String.valueOf(omsOrderItem.getOrderId()));
        paymentInfo.setTotalAmount(totalAmount);
        paymentInfo.setSubject(productName);
        paymentInfo.setCreateTime(new Date());
        paymentService.save(paymentInfo);
        // 发送检查支付结果的消息队列
        paymentService.sendDelayPaymentCheck(orderSn,5);
        return form;
    }

    @ApiOperation("支付成功后同步回调转支付成功页面")
    @ResponseBody
    @RequestMapping(value = "/alipay/callback/return",method = RequestMethod.GET)
    public CommonResult callBackReturn(HttpServletRequest request){// 页面同步反转的回调
        Map<String,String> paramsMap = new HashMap<String, String>();
        Map parameterMap = request.getParameterMap();
        for(Iterator<String> iter = parameterMap.keySet().iterator(); iter.hasNext();){
            String name = iter.next();
            String[] values = (String [])parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1 ) ? valueStr + values [i] : valueStr + values[i] + ",";
            }
            paramsMap.put(name,valueStr);
        }
        String outTradeNo = request.getParameter("out_trade_no");
        String tradeNo = request.getParameter("trade_no");
        String sign = request.getParameter("sign");
        try {
            boolean b = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key,AlipayConfig.charset,AlipayConfig.sign_type);// 对支付宝回调签名的校验
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //修改支付信息，幂等性检查
/*        boolean b = paymentService.checkPaymentStatus(outTradeNo);
        if(!b){
            PaymentInfo paymentInfo = paymentService.checkPaymentResult(outTradeNo);
            paymentService.updatePayment(request.getQueryString(),outTradeNo,tradeNo);
            //发送系统消息，出发并发商品支付业务消息队列
            paymentService.sendPaymentSuccess(outTradeNo,paymentInfo.getPaymentStatus(),tradeNo);
        }*/
        return CommonResult.success("支付成功");
    }
}

