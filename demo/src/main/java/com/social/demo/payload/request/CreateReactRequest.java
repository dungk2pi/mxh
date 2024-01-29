package com.social.demo.payload.request;

import lombok.Data;

@Data
public class CreateReactRequest {
  private String referenceId;

  private String postId;

  private Integer status;

  private String type;
}
