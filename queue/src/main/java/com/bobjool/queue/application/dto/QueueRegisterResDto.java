package com.bobjool.queue.application.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record QueueRegisterResDto(
	int member,
	int position,
	long rank,
	LocalDateTime createdAt
) {
	public static QueueRegisterResDto from(Map<String, Object> userinfo, long rank) {
		return new QueueRegisterResDto(
			(Integer)userinfo.get("member"),
			(Integer)userinfo.get("position"),
			rank,
			LocalDateTime.now()
		);
	}
}
