package com.bobjool.queue.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bobjool.queue.domain.entity.Queue;

public record QueueRegisterResDto(
	UUID queueId,
	int member,
	int position,
	long rank,
	LocalDateTime createdAt
) {
	public static QueueRegisterResDto from(Queue queue, long rank) {
		return new QueueRegisterResDto(
			queue.getId(),
			queue.getMember(),
			queue.getPosition(),
			rank,
			queue.getCreatedAt()
		);
	}
}
