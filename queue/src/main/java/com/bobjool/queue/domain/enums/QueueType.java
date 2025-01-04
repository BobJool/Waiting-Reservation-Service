package com.bobjool.queue.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueueType {
	KIOSK("매장등록"),
	ONLINE("온라인등록");

	private final String description;

	public static QueueType of(String request) {
		return switch (request) {
			case "KIOSK" -> KIOSK;
			case "ONLINE" -> ONLINE;
			default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_QUEUE_TYPE);
		};
	}
}
