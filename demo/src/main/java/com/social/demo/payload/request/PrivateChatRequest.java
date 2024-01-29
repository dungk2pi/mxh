package com.social.demo.payload.request;

import lombok.Data;

@Data
public class PrivateChatRequest {

  private Integer page;

  private String toId;

  private String type;

  private String msg;
}
