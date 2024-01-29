package com.social.demo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "saved_post")
@Data
public class SavedPost {
  @Id
  private String id;

  private String postId;

  private String status;

  private String userId;

  private Date saveDate;

}
