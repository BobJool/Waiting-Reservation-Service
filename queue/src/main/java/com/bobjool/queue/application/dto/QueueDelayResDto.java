package com.bobjool.queue.application.dto;

public record QueueDelayResDto(
	Long newRank,
	Long originalPosition,
	Integer member
) {
}
