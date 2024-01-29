package com.social.demo.services;

import com.social.demo.payload.request.CreateReactRequest;
import com.social.demo.payload.response.CommonResponse;

public interface CreateReactService {
  CommonResponse<Object> createReact(CreateReactRequest request, String username);
}
