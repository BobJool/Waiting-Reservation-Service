package com.bobjool.queue.domain.entity;

import java.util.UUID;

import com.bobjool.common.domain.entity.BaseEntity;
import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueStatus;
import com.bobjool.queue.domain.enums.QueueType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_queue")
public class Queue extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "restaurant_id", nullable = false)
	private UUID restaurantId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "position", nullable = false)
	private int position;

	@Column(name = "member", nullable = false)
	private int member;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private QueueStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private QueueType type;

	@Column(name = "delay_count", nullable = false)
	private int delayCount;

	@Enumerated(EnumType.STRING)
	@Column(name = "dining_option", nullable = false)
	private DiningOption diningOption;

}
