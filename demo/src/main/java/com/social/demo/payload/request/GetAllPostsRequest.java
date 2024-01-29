package com.social.demo.payload.request;

import lombok.Data;

@Data
public class GetAllPostsRequest {
  private Integer page;

  //for get post detail
  private String postId;
}
