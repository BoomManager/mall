package com.macro.mall.portal.config;

import com.macro.mall.portal.domain.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置
 * Created by macro on 2018/9/14.
 */
@Configuration
public class RabbitMqConfig {


    /**
     * 支付队列
     * @return
     */
    @Bean
    public Queue payQueue() {
        return new Queue(QueueEnum.QUEUE_PAY_CANCEL.getName());
    }

    /**
     * 绑定支付交互机
     * @return
     */
    @Bean
    DirectExchange payDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_PAY_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 将支付队列绑定到支付交互机
     * @param payDirect
     * @param payQueue
     * @return
     */
    @Bean
    Binding payBinding(DirectExchange payDirect,Queue payQueue){
        return BindingBuilder
                .bind(payQueue)
                .to(payDirect)
                .with(QueueEnum.QUEUE_PAY_CANCEL.getRouteKey());
    }

    @Bean
    public Queue paymentSuccessQueue() {
        return new Queue(QueueEnum.QUEUE_PAYMENT_SUCCESS_CANCEL.getName());
    }

    @Bean
    DirectExchange paymentSuccessDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_PAYMENT_SUCCESS_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    @Bean
    Binding paymentSuccessBinding(DirectExchange paymentSuccessDirect,Queue paymentSuccessQueue){
        return BindingBuilder
                .bind(paymentSuccessQueue)
                .to(paymentSuccessDirect)
                .with(QueueEnum.QUEUE_PAYMENT_SUCCESS_CANCEL.getRouteKey());
    }

    /**
     * 库存队列
     * @return
     */
    @Bean
    public Queue reserveQueue() {
        return new Queue(QueueEnum.QUEUE_STOCK_QUEUE.getName());
    }

    /**
     * 绑定库存交互机
     * @return
     */
    @Bean
    DirectExchange reserveDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_STOCK_QUEUE.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 将库存队列绑定到库存交换机
     * @param reserveDirect
     * @param reserveQueue
     * @return
     */
    @Bean
    Binding reserveBinding(DirectExchange reserveDirect,Queue reserveQueue){
        return BindingBuilder
                .bind(reserveQueue)
                .to(reserveDirect)
                .with(QueueEnum.QUEUE_STOCK_QUEUE.getRouteKey());
    }

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange orderDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单延迟队列队列所绑定的交换机
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单实际消费队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    /**
     * 订单延迟队列（死信队列）
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())//到期后转发的路由键
                .build();
    }


    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect,Queue orderQueue){
        return BindingBuilder
                .bind(orderQueue)
                .to(orderDirect)
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect,Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

}
