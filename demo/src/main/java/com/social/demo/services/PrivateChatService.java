package com.social.demo.services;

import com.social.demo.payload.request.PrivateChatRequest;
import com.social.demo.payload.response.CommonResponse;

public interface PrivateChatService {
  CommonResponse<Object> getChatDetail(PrivateChatRequest request, String username);

  CommonResponse<Object> sendMessage(PrivateChatRequest request, String username);
}
