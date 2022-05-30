package com.farmer.seckill.controller.limit;

import com.farmer.seckill.inout.CommonJsonResponse;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;


@Component
@Aspect
public class LimiterAop {


    private static final ConcurrentHashMap<String, RateLimiter> map = new ConcurrentHashMap<>();


    @Pointcut("@annotation(FarmerLimiter)")
    public void cut(){}

    @Around("cut()")
    public Object deal(ProceedingJoinPoint point) throws Throwable {
        final MethodSignature signature = (MethodSignature) point.getSignature();
        final FarmerLimiter annotation = signature.getMethod().getAnnotation(FarmerLimiter.class);
        final int value = annotation.value();
        final String name = signature.getMethod().toString();
        RateLimiter rateLimiter;
        if(map.containsKey(name)){
            rateLimiter = map.get(name);
        }else{
            rateLimiter = RateLimiter.create(value);
            map.put(name, rateLimiter);
        }

        if(rateLimiter.tryAcquire()){
            return point.proceed();
        }else{
            return new CommonJsonResponse("0000","活动太火爆，稍后再试");
        }
    }
}
