package com.bobjool.queue.infrastructure.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

	private final RedissonClient redissonClient;

	@Around("@annotation(com.bobjool.queue.infrastructure.aspect.RedissonLock)")
	public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		RedissonLock annotation = method.getAnnotation(RedissonLock.class);

		// SpEL 표현식으로 복합 락 키 생성 (prefix 추가)
		String lockKey = annotation.prefix() + generateLockKey(signature.getParameterNames(), joinPoint.getArgs(), annotation.value());
		RLock lock = redissonClient.getLock(lockKey);

		try {
			if (lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.MILLISECONDS)) {
				log.info("Lock acquired for key: {}", lockKey);
				return joinPoint.proceed();
			} else {
				log.warn("Failed to acquire lock for key: {}", lockKey);
				throw new IllegalStateException("Unable to acquire lock for key: " + lockKey);
			}
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
				log.info("Lock released for key: {}", lockKey);
			}
		}
	}

	private String generateLockKey(String[] paramNames, Object[] args, String expression) {
		// SpEL 파서를 사용하여 동적 키 생성
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();

		// 메서드 파라미터를 SpEL 컨텍스트에 설정
		for (int i = 0; i < paramNames.length; i++) {
			context.setVariable(paramNames[i], args[i]);
		}

		// SpEL 표현식 평가 및 결과 반환
		return parser.parseExpression(expression).getValue(context, String.class);
	}
}
