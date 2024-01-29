package com.social.demo.models;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Document(collection = "posts")
@Data
public class Posts {
  @Id
  private String id;

  private String userId;

  private String content;

  private String status;

  private String reportStatus;

  private Date createDate;

  private Date editDate;

  private String byadmin;

  private String reason;

  private Date lastReactDate;
}
