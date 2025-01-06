package com.bobjool.restaurant.domain.entity.restaurantSchedule;


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

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "restaurant_id", columnDefinition = "UUID", updatable = false, nullable = false)
  private UUID restaurantId;

  @Column(name = "table_number", nullable = false)
  private int tableNumber;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "time_slot", nullable = false)
  private LocalTime timeSlot;

  @Column(name = "max_capacity", nullable = false)
  private int maxCapacity;

  @Column(name = "current_capacity")
  private int currentCapacity;

  @Column(name = "available", nullable = false)
  private boolean available;

  public static RestaurantSchedule create(
   UUID restaurantId,
   int tableNumber,
   LocalDate date,
   LocalTime timeSlot,
   int maxCapacity,
   int currentCapacity,
   boolean available
  ) {
    return RestaurantSchedule.builder()
        .restaurantId(restaurantId)
        .tableNumber(tableNumber)
        .date(date)
        .timeSlot(timeSlot)
        .maxCapacity(maxCapacity)
        .currentCapacity(currentCapacity)
        .available(available)
        .build();
  }

  //Customer가 좌석 예약
  public void reserve(
      Long userId,
      int currentCapacity
  ){
      this.userId = userId;
      this.currentCapacity = currentCapacity;
      this.available = false;
  }

  //Owner가 스케쥴에 대한 정보를 수정
  public void update(
    LocalDate date,
    LocalTime timeSlot,
    int maxCapacity,
    int currentCapacity,
    boolean available
  ){
    this.date = date;
    this.timeSlot = timeSlot;
    this.maxCapacity = maxCapacity;
    this.currentCapacity = currentCapacity;
    this.available = available;
  }

  /**
   * 예약 가능 여부를 확인하는 메서드
   * @param requestedCapacity 요청된 추가 예약 인원
   * @return true: 수용 가능, false: 초과
   */
  public boolean isCapacityExceeded(int requestedCapacity) {
    return this.maxCapacity > requestedCapacity;
  }

}
