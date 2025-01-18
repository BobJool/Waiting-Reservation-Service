package com.bobjool.queue.application.client;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.queue.application.dto.RestaurantCheckValidReqDto;
import com.bobjool.queue.application.dto.RestaurantValidResDto;
import com.bobjool.queue.application.dto.RestaurantCheckOwnerReqDto;

public interface RestaurantClient {
	ApiResponse<RestaurantValidResDto> restaurantValidCheck(RestaurantCheckValidReqDto request);
	boolean restaurantOwnerCheck(RestaurantCheckOwnerReqDto restaurantCheckOwnerReqDto);

}
