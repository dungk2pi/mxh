package com.social.demo.payload.request;

import lombok.Data;

@Data
public class AddFriendRequest {
  private String userRevId;

  // 1. add + remove: add friend
  // 2. accept + reject: handle friend
  // 3. unfr: unfriend
  private String flag;

  private String userOwnId;
}
