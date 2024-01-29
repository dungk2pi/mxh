package com.social.demo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "tag")
@Data
public class Tag {
  @Id
  private String id;

  private String referenceId;

  private String status;

  private String userReq;

  private String userRes;

  private Integer seq;

  private Date createDate;

}
