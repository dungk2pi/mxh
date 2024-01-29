package com.social.demo.services;

import com.social.demo.payload.request.LoginRequest;
import com.social.demo.payload.request.RegisterRequest;
import com.social.demo.payload.response.CommonResponse;

public interface AuthService {
  CommonResponse<Object> login(LoginRequest request);

  CommonResponse<Object> register(RegisterRequest request);
}
