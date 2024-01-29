package com.social.demo.services;

import com.social.demo.payload.request.GetMyPostsRequest;
import com.social.demo.payload.response.CommonResponse;

public interface GetMyPostsService {
  CommonResponse<Object> getMyPosts(GetMyPostsRequest request, String username);
}
