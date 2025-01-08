package com.bobjool.queue.domain.enums;

import java.util.List;
import java.util.Map;

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
	DELAYED("줄미루기"),
	ALERTED("입장호출"),
	RUSH_SENT("입장재촉");

	private final String description;

	// 허용되는 상태 전환 정의
	private static final Map<QueueStatus, List<QueueStatus>> TRANSITIONS = Map.of(
		WAITING, List.of(DELAYED, ALERTED, RUSH_SENT,CHECK_IN, CANCELED),
		DELAYED, List.of(ALERTED, CHECK_IN, RUSH_SENT, CANCELED),
		ALERTED, List.of(RUSH_SENT, CHECK_IN, CANCELED),
		RUSH_SENT, List.of(CHECK_IN, CANCELED),
		CHECK_IN, List.of(),
		CANCELED, List.of()
	);

	public boolean canTransitionTo(QueueStatus newStatus) {
		return TRANSITIONS.getOrDefault(this, List.of()).contains(newStatus);
	}

	public static QueueStatus of(String request) {
		return switch (request) {
			case "WAITING" -> WAITING;
			case "CHECK_IN" -> CHECK_IN;
			case "CANCELED" -> CANCELED;
			case "DELAYED" -> DELAYED;
			case "ALERTED" -> ALERTED;
			case "RUSH_SENT" -> RUSH_SENT;
			default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_QUEUE_STATUS);
		};
	}
}
