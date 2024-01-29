package com.social.demo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "media")
@Data
public class Media {
  @Id
  private String id;

  private String referenceId;

  private String type;

  //video or image
  private String status;

  private String mediaUrl;

  private Integer seq;

  private String description;

  private Date createDate;
}
