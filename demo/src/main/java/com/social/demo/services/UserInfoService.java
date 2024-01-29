package com.social.demo.services;

import com.social.demo.payload.request.UserInfoRequest;
import com.social.demo.payload.response.CommonResponse;

public interface UserInfoService {
  CommonResponse<Object> getUserInfo(UserInfoRequest request, String username);

  CommonResponse<Object> getUserPosts(UserInfoRequest request, String username);

  CommonResponse<Object> getUserImages(UserInfoRequest request, String username);

  CommonResponse<Object> getUserVideos(UserInfoRequest request, String username);

  CommonResponse<Object> getUserSettings(UserInfoRequest request,String username);
}
