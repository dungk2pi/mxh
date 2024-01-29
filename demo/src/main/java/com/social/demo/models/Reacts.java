package com.social.demo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document(collection = "reacts")
@Data
public class Reacts {
    @Id
    private String id;

    @NotNull
	private String userId;

    private String referenceId;

    private String type;

    private Integer status;

    private Date createDate;

    private Date updateDate;
}
