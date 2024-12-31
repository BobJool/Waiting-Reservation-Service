package com.bobjool.restaurant.domain.entity;

import com.bobjool.common.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "p_restaurant")
public class Restaurant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "restaurant_category", nullable = false)
  private RestaurantCategory restaurantCategory;

  @Column(name = "restaurant_number", nullable = false)
  private String restaurantNumber;

  @Column(name = "restaurant_name", nullable = false)
  private String restaurantName;

  @Column(name = "restaurant_region", nullable = false)
  private RestaurantRegion restaurantRegion;

  @Column(name = "restaurant_address_detail", nullable = false)
  private String restaurantAddressDetail;

  @Column(name = "restaurant_phone", nullable = false)
  private String restaurantPhone;

  @Column(name = "restaurant_description", nullable = false)
  private String restaurantDescription;

  @Column(name = "restaurant_volume", nullable = false)
  private String restaurantVolume;

  @Column(name = "is_reservation", nullable = false)
  private boolean isReservation;

  @Column(name = "is_queue", nullable = false)
  private boolean isQueue;

  @Column(name = "open_time", nullable = false)
  private LocalDateTime openTime;

  @Column(name = "close_time", nullable = false)
  private LocalDateTime closeTime;

}
