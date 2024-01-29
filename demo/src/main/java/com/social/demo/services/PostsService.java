package com.social.demo.services;

import com.social.demo.payload.request.HiddenRequest;
import com.social.demo.payload.response.CommonResponse;

public interface PostsService {
    CommonResponse<Object> deletePostById(String id, String username);
}
