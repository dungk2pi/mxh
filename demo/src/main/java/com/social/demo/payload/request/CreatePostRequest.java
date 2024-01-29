package com.social.demo.payload.request;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String content;
    private String status;
    private ArrayList<HashMap<String, String>> images;
    private ArrayList<HashMap<String, String>> tag;
}
