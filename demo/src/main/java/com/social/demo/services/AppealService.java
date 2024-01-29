package com.social.demo.services;

import com.social.demo.payload.request.AppealRequest;
import com.social.demo.payload.response.CommonResponse;

public interface AppealService {
  CommonResponse<Object> appealPost(AppealRequest request, String username);
}
