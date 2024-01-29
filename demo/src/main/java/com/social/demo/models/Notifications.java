package com.social.demo.models;

import java.util.Date;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "notification")
@Data
public class Notifications {
  @Id
  private String id;

  private String userRevId;

  private String userReqId;

  private String content;

  private String referenceId;

  private Integer status;

  private boolean isRead;

  private Date createDate;

  private Date editDate;
}
