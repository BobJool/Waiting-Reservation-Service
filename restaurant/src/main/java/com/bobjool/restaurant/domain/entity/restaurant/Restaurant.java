package com.bobjool.restaurant.domain.entity.restaurant;

import com.bobjool.common.domain.entity.BaseEntity;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantUpdateDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "p_restaurant")
public class Restaurant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "restaurant_category", nullable = false)
  private RestaurantCategory restaurantCategory;

  @Column(name = "restaurant_phone", nullable = false)
  private String restaurantPhone;

  @Column(name = "restaurant_name", nullable = false)
  private String restaurantName;

  @Enumerated(EnumType.STRING)
  @Column(name = "restaurant_region", nullable = false)
  private RestaurantRegion restaurantRegion;

  @Column(name = "restaurant_address_detail", nullable = false)
  private String restaurantAddressDetail;

  @Column(name = "restaurant_description", nullable = false)
  private String restaurantDescription;

  @Column(name = "restaurant_volume", nullable = false)
  private int restaurantVolume;

  @Column(name = "is_reservation", nullable = false)
  private boolean isReservation;

  @Column(name = "is_queue", nullable = false)
  private boolean isQueue;

  @Column(name = "open_time", nullable = false)
  private LocalTime openTime;

  @Column(name = "close_time", nullable = false)
  private LocalTime closeTime;

  public static Restaurant create(
      Long userId,
      RestaurantCategory restaurantCategory,
      String restaurantPhone,
      String restaurantName,
      RestaurantRegion restaurantRegion,
      String restaurantAddressDetail,
      String restaurantDescription,
      int restaurantVolume,
      boolean isReservation,
      boolean isQueue,
      LocalTime openTime,
      LocalTime closeTime
  ) {
    return Restaurant.builder()
        .userId(userId)
        .restaurantCategory(restaurantCategory)
        .restaurantPhone(restaurantPhone)
        .restaurantName(restaurantName)
        .restaurantRegion(restaurantRegion)
        .restaurantAddressDetail(restaurantAddressDetail)
        .restaurantDescription(restaurantDescription)
        .restaurantVolume(restaurantVolume)
        .isReservation(isReservation)
        .isQueue(isQueue)
        .openTime(openTime)
        .closeTime(closeTime)
        .build();
  }

  public void update(RestaurantUpdateDto restaurantUpdateDto){
    this.restaurantCategory = restaurantUpdateDto.restaurantCategory();
    this.restaurantPhone = restaurantUpdateDto.restaurantPhone();
    this.restaurantName = restaurantUpdateDto.restaurantName();
    this.restaurantRegion = restaurantUpdateDto.restaurantRegion();
    this.restaurantAddressDetail = restaurantUpdateDto.restaurantAddressDetail();
    this.restaurantDescription = restaurantUpdateDto.restaurantDescription();
    this.restaurantVolume = restaurantUpdateDto.restaurantVolume();
    this.isReservation = restaurantUpdateDto.isReservation();
    this.isQueue = restaurantUpdateDto.isQueue();
    this.openTime = restaurantUpdateDto.openTime();
    this.closeTime = restaurantUpdateDto.closeTime();
  }

}
