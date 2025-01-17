package com.bobjool.queue.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CancelType {
	COSTUMER("고객의 요청으로"),
	ADMIN("관리자의 요청으로"),
	SYSTEM("입장시간이 경과되어 자동으로");

	private final String description;

	public static CancelType fromRole(String role) {
		return switch (role) {
			case "MASTER" -> ADMIN;
			case "OWNER" -> ADMIN;
			default -> COSTUMER;
		};
	}
}
