package com.bobjool.queue.application.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QueueCancelDto(

	@JsonProperty("restaurantId")
	UUID restaurantId,
	@JsonProperty("userId")
	Long userId,
	@JsonProperty("reason")
	String reason
) { }
