package com.social.demo.payload.request;

import lombok.Data;

@Data
public class GetCommentByReferenceIdRequest {
    private int page;
    private String referenceId;
}
