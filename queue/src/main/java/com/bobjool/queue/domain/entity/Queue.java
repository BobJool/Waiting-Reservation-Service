package com.bobjool.queue.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bobjool.common.domain.entity.BaseEntity;
import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueStatus;
import com.bobjool.queue.domain.enums.QueueType;

import jakarta.persistence.*;
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
	private Integer position;

	@Column(name = "member", nullable = false)
	private Integer member;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private QueueStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private QueueType type;

	@Column(name = "delay_position")
	private Integer delayPosition;

	@Column(name = "delay_count", nullable = false)
	private Integer delayCount;

	@Enumerated(EnumType.STRING)
	@Column(name = "dining_option", nullable = false)
	private DiningOption diningOption;


}
