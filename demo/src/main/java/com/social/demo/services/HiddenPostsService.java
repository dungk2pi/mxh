package com.social.demo.services;

import com.social.demo.payload.request.HiddenRequest;
import com.social.demo.payload.response.CommonResponse;

public interface HiddenPostsService {
    CommonResponse<Object> hiddenPost(HiddenRequest dto, String username);
}
