package com.bobjool.queue.application.dto;

import java.util.UUID;

import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record QueueDelayDto(

	@JsonProperty("restaurantId")
	UUID restaurantId,
	@JsonProperty("userId")
	Long userId,
	@JsonProperty("targetUserId")
	Long targetUserId
) { }
