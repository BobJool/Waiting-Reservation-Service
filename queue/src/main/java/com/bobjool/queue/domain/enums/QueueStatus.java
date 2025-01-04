package com.bobjool.queue.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueueStatus {
	WAITING("대기중"),
	CHECK_IN("입장"),
	CANCELED("줄서기취소"),
	DELAYED("줄미루기");

	private final String description;

	public static QueueStatus of(String request) {
		return switch (request) {
			case "WAITING" -> WAITING;
			case "CHECK_IN" -> CHECK_IN;
			case "CANCELED" -> CANCELED;
			case "DELAYED" -> DELAYED;
			default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_QUEUE_STATUS);
		};
	}
}
