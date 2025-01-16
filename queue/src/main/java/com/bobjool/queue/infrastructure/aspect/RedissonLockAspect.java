package com.bobjool.queue.infrastructure.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

	private static final String REDISSON_LOCK_PREFIX = "LOCK:";

	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(com.bobjool.queue.infrastructure.aspect.RedissonLock)")
	public Object redissonLock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		RedissonLock annotation = method.getAnnotation(RedissonLock.class);

		String lockKey = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(),
			joinPoint.getArgs(), annotation.value());
		RLock lock = redissonClient.getLock(lockKey);

		try {
			boolean available = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit());
			log.info("RedissonLockAspect Lock acquired for key: {}", lockKey);
			if (!available) {
				return false;
			}
			return aopForTransaction.proceed(joinPoint);
		} catch (InterruptedException e) {
			log.warn("RedissonLockAspect Failed to acquire lock for key: {}", lockKey);
			throw new IllegalStateException("Unable to acquire lock for key: " + lockKey);
		} finally {
			try {
				lock.unlock();
				log.info("RedissonLockAspect Lock released for key: {}", lockKey);
			} catch (IllegalStateException e) {
				log.info("RedissonLockAspect Lock Already UnLock: {} {}", method.getName(), lockKey);
			}
		}
	}

}
