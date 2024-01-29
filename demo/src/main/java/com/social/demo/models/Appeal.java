package com.social.demo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "appeal_post")
@Data
public class Appeal {
  @Id
  private String id;

  private String content;

  private String userId;

  private String referenceId;

  private Integer status;

  private Date createDate;

  private Date ediDate;

  //userId admin
  private String byadmin;

  //admin read
  private Integer isRead;
}
