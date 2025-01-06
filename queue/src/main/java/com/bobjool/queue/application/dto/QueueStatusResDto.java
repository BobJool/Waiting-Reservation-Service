package com.bobjool.queue.application.dto;

import java.util.List;

public record QueueStatusResDto(
	long userRank,
	List<String> nextUsers
) {
}
