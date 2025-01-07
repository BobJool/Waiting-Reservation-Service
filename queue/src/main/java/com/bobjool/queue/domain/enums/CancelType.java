package com.bobjool.queue.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CancelType {
	COSTUMER("고객의 요청으로"),
	OWNER("관리자의 요청으로"),
	SYSTEM("입장시간이 경과되어 자동으로");

	private final String description;

	public static CancelType of(String request) {
		return switch (request) {
			case "COSTUMER" -> COSTUMER;
			case "OWNER" -> OWNER;
			case "SYSTEM" -> SYSTEM;
			default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_QUEUE_TYPE);
		};
	}
}
