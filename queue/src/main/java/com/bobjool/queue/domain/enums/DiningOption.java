package com.bobjool.queue.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiningOption {
	IN_STORE("매장이용"),
	TAKEOUT("포장"),
	DELIVERY("배달");

	private final String description;

	public static DiningOption of(String request) {
		return switch (request) {
			case "IN_STORE" -> IN_STORE;
			case "TAKEOUT" -> TAKEOUT;
			case "DELIVERY" -> DELIVERY;
			default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_DINING_OPTION);
		};
	}

}
