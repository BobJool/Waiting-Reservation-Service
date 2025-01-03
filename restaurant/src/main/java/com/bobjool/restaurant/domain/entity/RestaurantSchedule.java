package com.bobjool.restaurant.domain.entity;


import com.bobjool.common.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_restaurant_schedule")
public class RestaurantSchedule extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "restaurant_id", columnDefinition = "UUID", updatable = false, nullable = false)
  private UUID restaurantId;

  @Column(name = "table_number", nullable = false)
  private int tableNumber;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "time_slot", nullable = false)
  private LocalTime timeSlot;

  @Column(name = "max_table_capacity", nullable = false)
  private int maxTableCapacity;

  @Column(name = "current_capacity")
  private int currentCapacity;

  @Column(name = "available", nullable = false)
  private boolean available;
}
