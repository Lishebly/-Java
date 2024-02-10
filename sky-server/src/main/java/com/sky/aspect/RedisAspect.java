package com.sky.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
@Slf4j
public class RedisAspect {

    @Pointcut("execution(* com.sky.controller.admin.*.*(..)) && @annotation(com.sky.annotation.RedisFill)")
    public void autoDeleteCache(){}

    @Autowired
    private RedisTemplate redisTemplate;
    @Around("autoDeleteCache()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("开始删除缓存");
        Object result = joinPoint.proceed();
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return result;
    }
}
