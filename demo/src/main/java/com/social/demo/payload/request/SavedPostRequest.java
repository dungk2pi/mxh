package com.social.demo.payload.request;

import lombok.Data;

@Data
public class SavedPostRequest {

  private String postId;

  private String flag;

  private Integer page;
}
