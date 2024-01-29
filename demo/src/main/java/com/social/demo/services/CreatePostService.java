package com.social.demo.services;

import com.social.demo.payload.request.CreatePostRequest;
import com.social.demo.payload.response.CommonResponse;

public interface CreatePostService {
  CommonResponse<Object> createNewPost(CreatePostRequest request, String username);
}
