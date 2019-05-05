package com.macro.mall.component;

import com.macro.mall.common.api.CommonResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * HibernateValidator错误结果处理切面
 * Created by macro on 2018/4/26.
 */
@Aspect
@Component
@Order(2)
public class BindingResultAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(public * com.macro.mall.controller.*.*(..))")
    public void BindingResult() {

    }

    /**
     * 参数验证，失败返回结果，提示信息
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("BindingResult()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult result = (BindingResult) arg;
                if (result.hasErrors()) {
                    FieldError fieldError = result.getFieldError();
                    if(fieldError!=null){
                        return CommonResult.validateFailed(fieldError.getDefaultMessage());
                    }else{
                        return CommonResult.validateFailed();
                    }
                }
            }
        }
        return joinPoint.proceed();
    }
}
