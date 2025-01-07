package com.bobjool.queue.application.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QueueCheckInDto(
	@JsonProperty("restaurantId")
	UUID restaurantId,
	@JsonProperty("userId")
	Long userId
) { }
