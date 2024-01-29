package com.social.demo.services;

import com.social.demo.payload.request.SavedPostRequest;
import com.social.demo.payload.response.CommonResponse;

public interface SavedPostService {
  CommonResponse<Object> saveOrUnsavePost(SavedPostRequest request, String username);

  CommonResponse<Object> getSavePost(SavedPostRequest request, String username);

}
