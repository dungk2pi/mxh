package com.social.demo.services;

import com.social.demo.payload.request.ResetPasswordRequest;
import com.social.demo.payload.response.CommonResponse;

public interface ResetPasswordService {
  CommonResponse<Object> updateResetPasswordToken(ResetPasswordRequest request, int token);

  CommonResponse<Object> checkMatchToken(ResetPasswordRequest request);

  CommonResponse<Object> setNewPassword(ResetPasswordRequest request);

}
