package com.social.demo.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "hidden_posts")
@Data
public class HiddenPost {
    @Id
    private String id;

    private String userId;

    private String postId;

    private Date hiddenDate;

    private String status;
}
