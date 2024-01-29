package com.social.demo.services;

import org.springframework.stereotype.Service;

import com.social.demo.payload.request.GetAllPostsRequest;
import com.social.demo.payload.response.CommonResponse;

@Service
public interface GetAllPostsService {
  CommonResponse<Object> getAllPosts(GetAllPostsRequest request, String username);

  CommonResponse<Object> getPostDetail(GetAllPostsRequest request, String username);

}
