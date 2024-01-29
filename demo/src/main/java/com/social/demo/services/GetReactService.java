package com.social.demo.services;

import com.social.demo.payload.request.GetReactRequest;
import com.social.demo.payload.response.CommonResponse;

public interface GetReactService {
  CommonResponse<Object> getReacts(GetReactRequest request, String username);
}
