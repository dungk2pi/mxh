package com.social.demo.services;

import com.social.demo.payload.request.UserIntroRequest;
import com.social.demo.payload.response.CommonResponse;

public interface UserIntroService {
  CommonResponse<Object> getUserIntro(UserIntroRequest request, String username);
}
