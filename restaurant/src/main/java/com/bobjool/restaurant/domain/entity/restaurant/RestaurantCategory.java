package com.bobjool.restaurant.domain.entity.restaurant;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RestaurantCategory {

  //임시 음식 카테고리
  KOREA,
  JAPAN

//  private final String Category;
//
//  RestaurantCategory(String Category){
//    this.Category = Category;
//  }
//
//  @JsonValue
//  public String  getCategory(){
//    return Category;
//  }
//
//  @JsonCreator
//  public static RestaurantCategory fromValue(String value) {
//    for (RestaurantCategory category : RestaurantCategory.values()) {
//      if (category.Category.equalsIgnoreCase(value)) {
//        return category;
//      }
//    }
//    throw new BobJoolException(ErrorCode.INVALID_CATEGORY);
//  }

}
