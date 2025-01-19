package com.bobjool.restaurant.infrastructure.aspect;

import com.bobjool.restaurant.infrastructure.config.redis.AopForTransaction;
import com.bobjool.restaurant.infrastructure.config.redis.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.bobjool.restaurant.infrastructure.aspect.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(
                signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key()
        );
        RLock rLock = redissonClient.getLock(key);

        try {
            log.info("락 획득 시도: {}", key);
            boolean available = rLock.tryLock(
                    distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit()
            );
            if (!available) {
                return false;
            }
            log.info("락 획득 성공: {}", key);
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
                log.info("락 해제: {}", key);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock {} {}", method.getName(), key);
            }
        }
    }
}