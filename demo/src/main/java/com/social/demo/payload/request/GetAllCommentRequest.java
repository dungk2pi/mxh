package com.social.demo.payload.request;

import lombok.Data;

@Data
public class GetAllCommentRequest {
    private Integer page;
    private String postId;
    private Integer level;
}
