package com.macro.mall.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 修改订单费用信息参数
 * Created by macro on 2018/10/29.
 */
@Getter
@Setter
public class OmsMoneyInfoParam {
    private Long orderId;
    private BigDecimal freightAmount;//货物数量
    private BigDecimal discountAmount;//折扣数量
    private Integer status;
}
