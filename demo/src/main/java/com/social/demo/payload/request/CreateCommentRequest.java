package com.social.demo.payload.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class CreateCommentRequest {
    private String content;
    private int level;
    private String status;
    private String postId;// id bài post
    private String referenceId;// id thằng cmt bố
    private ArrayList<HashMap<String, String>> images;
    private ArrayList<HashMap<String, String>> tag; // id của những thằng tag
}
