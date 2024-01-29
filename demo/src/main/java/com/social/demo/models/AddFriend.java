package com.social.demo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "add_friend")
@Data
public class AddFriend {
  @Id
  private String id;

  private String userReqId;

  private String userRevId;

  private Date createDate;
}
