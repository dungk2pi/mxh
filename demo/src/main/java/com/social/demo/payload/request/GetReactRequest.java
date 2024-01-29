package com.social.demo.payload.request;

import lombok.Data;

@Data
public class GetReactRequest {
    private Integer page;

    private String referenceId;
}
