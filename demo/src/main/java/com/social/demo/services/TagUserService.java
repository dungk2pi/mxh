package com.social.demo.services;

import com.social.demo.payload.request.TagUserRequest;
import com.social.demo.payload.response.CommonResponse;

public interface TagUserService {
  CommonResponse<Object> tagUser(TagUserRequest request, String username);
}
