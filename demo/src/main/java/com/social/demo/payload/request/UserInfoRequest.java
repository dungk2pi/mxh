package com.social.demo.payload.request;

import lombok.Data;

@Data
public class UserInfoRequest {
    private String userId;

    private Integer page;
}
