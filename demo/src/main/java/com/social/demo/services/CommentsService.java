package com.social.demo.services;

import com.social.demo.payload.request.CreateCommentRequest;
import com.social.demo.payload.request.GetAllCommentRequest;
import com.social.demo.payload.request.GetCommentByReferenceIdRequest;
import com.social.demo.payload.response.CommonResponse;
import org.springframework.stereotype.Service;

@Service
public interface CommentsService {
    CommonResponse<Object> createComment(CreateCommentRequest dto, String userName);

    CommonResponse<Object> getAllComment(GetAllCommentRequest request, String username);

    CommonResponse<Object> getCmtByReferenceId(GetCommentByReferenceIdRequest referenceId, String userName);

    CommonResponse<Object> deleteCommentById(String id, String username);
}
