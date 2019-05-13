package com.macro.mall.portal.domain;


/**
 * 消息队列枚举配置
 * Created by macro on 2018/9/14.
 */
public enum QueueEnum {
    /**
     * 消息通知队列
     */
    QUEUE_ORDER_CANCEL("mall.order.direct", "mall.order.cancel", "mall.order.cancel"),
    /**
     * 消息通知ttl队列
     */
    QUEUE_TTL_ORDER_CANCEL("mall.order.direct.ttl", "mall.order.cancel.ttl", "mall.order.cancel.ttl"),

    /**
     * 支付通知队列
     */
    QUEUE_PAY_CANCEL("mall.pay.direct","mall.pay.cancel","mall.pay.cancel"),

    /**
     * 支付通知ttl队列
     */
    QUEUE_TTL_PAY_CANCEL("mall.pay.direct.ttl","mall.pay.cancel.ttl","mall.pay.cancel.ttl"),

    /**
     * 支付成功通知队列
     */
    QUEUE_PAYMENT_SUCCESS_CANCEL("mall.paysuccess.direct","mall.paysuccess.cancel","mall.paysuccess.cancel"),

    /**
     * 库存通知队列
     */
    QUEUE_STOCK_QUEUE("mall.stock.direct","mall.stock.cancel","mall.stock.cancel");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }

    public String getExchange() {
        return exchange;
    }

    public String getName() {
        return name;
    }

    public String getRouteKey() {
        return routeKey;
    }}
