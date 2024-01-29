package com.social.demo.models;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "private_chat")
@Data
public class PrivateChat {
  private String mergeId;

  private String fromId;

  private String toId;

  private Date sentDate;

  private Date readDate;

  private String type;

  private String msg;
}
