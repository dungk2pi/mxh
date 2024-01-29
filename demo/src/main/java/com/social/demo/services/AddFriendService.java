package com.social.demo.services;

import com.social.demo.payload.request.AddFriendRequest;
import com.social.demo.payload.response.CommonResponse;

public interface AddFriendService {
  CommonResponse<Object> requestAddFriend(AddFriendRequest request, String username);

  CommonResponse<Object> handleAddFriend(AddFriendRequest request, String username);

  CommonResponse<Object> unFriend(AddFriendRequest request, String username);

  CommonResponse<Object> listFriend(AddFriendRequest request, String username);

  CommonResponse<Object> listFriendReq(AddFriendRequest request, String username);
}
