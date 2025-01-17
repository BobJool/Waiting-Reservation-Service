package com.bobjool.queue.infrastructure.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 분산 락을 적용하기 위한 커스텀 애노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
	String value(); // 락 키

	TimeUnit timeUnit() default TimeUnit.SECONDS;

	long waitTime() default 20L; // 락 대기 시간 (밀리초)

	long leaseTime() default 3L; // 락 유지 시간 (밀리초)
}
