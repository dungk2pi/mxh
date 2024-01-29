package com.social.demo.payload.request;

import lombok.Data;

@Data
public class TagUserRequest {
  private Integer page;
  private String name;
}
