package com.social.demo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "comments")
@Data
public class Comments {
  @Id
  private String id;

  private String userId;

  private String postId;

  private String status;

  private Integer level;

  private String referenceId;

  private String content;

  private Date createDate;

  private Date editDate;
}
