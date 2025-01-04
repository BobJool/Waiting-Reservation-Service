package com.bobjool.queue.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bobjool.queue.domain.entity.Queue;
import com.bobjool.queue.domain.repository.QueueRepository;

public interface QueueRepositoryImpl extends JpaRepository<Queue, UUID>, QueueRepository, QueueRepositoryCustom {

}
