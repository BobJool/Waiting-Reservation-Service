package com.bobjool.queue.infrastructure.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redisson 분산 락을 적용하기 위한 커스텀 애노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
	String value(); // 락 키
	String prefix() default ""; // 접두사
	long waitTime() default 3000; // 락 대기 시간 (밀리초)
	long leaseTime() default 50000; // 락 유지 시간 (밀리초)
}
