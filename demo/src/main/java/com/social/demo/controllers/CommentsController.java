package com.social.demo.controllers;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.payload.request.*;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.security.jwt.JwtUtils;
import com.social.demo.services.CommentsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/func/cmt")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentsController {
    private final Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CommentsService commentsService;

    @PostMapping("/createCmt")
    public CommonResponse<Object> createNewCmt(@RequestBody CreateCommentRequest request, @RequestHeader Map<String, String> header){
        CommonResponse<Object> response = new CommonResponse<Object>();
        try {
            String bearerToken = header.get("authorization");
            log.info("-------> bearerToken {}", bearerToken);
            String token = jwtUtils.getTokenFromBearerToken(bearerToken);
            log.info("-------> token {}", token);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            log.info("-------> username {}", username);
            response = commentsService.createComment(request, username);
            return response;
        } catch (Exception e) {
            response.setEcode(EcodeConstant.EXCEPTION);
            response.setMessage(EcodeConstant.EXCEPTION_MSG);
            return response;
        }
    }

    @PostMapping("/getAllComment")
    public CommonResponse<Object> getAllComment(@RequestBody GetAllCommentRequest request, @RequestHeader Map<String, String> header){
        CommonResponse<Object> response = new CommonResponse<Object>();
        try {
            String bearerToken = header.get("authorization");
            log.info("-------> bearerToken {}", bearerToken);
            String token = jwtUtils.getTokenFromBearerToken(bearerToken);
            log.info("-------> token {}", token);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            log.info("-------> username {}", username);
            response = commentsService.getAllComment(request ,username ) ;

            return response;
        } catch (Exception e) {
            response.setEcode(EcodeConstant.EXCEPTION);
            response.setMessage(EcodeConstant.EXCEPTION_MSG);
            return response;
        }
    }



    @PostMapping("/getCommentByReferenceId")
    public CommonResponse<Object> getCommentByReferenceId(@RequestBody GetCommentByReferenceIdRequest request, @RequestHeader Map<String, String> header){
        CommonResponse<Object> response = new CommonResponse<Object>();
        try {
            String bearerToken = header.get("authorization");
            log.info("-------> bearerToken {}", bearerToken);
            String token = jwtUtils.getTokenFromBearerToken(bearerToken);
            log.info("-------> token {}", token);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            log.info("-------> username {}", username);
            response =commentsService.getCmtByReferenceId(request ,username ) ;

            return response;
        } catch (Exception e) {
            response.setEcode(EcodeConstant.EXCEPTION);
            response.setMessage(EcodeConstant.EXCEPTION_MSG);
            return response;
        }
    }

    @DeleteMapping("/deleteCommentById")
    public CommonResponse<Object> deleteCommentById(@RequestBody IdRequest dto, @RequestHeader Map<String, String> header){
        CommonResponse<Object> response = new CommonResponse<Object>();
        try {
            String bearerToken = header.get("authorization");
            log.info("-------> bearerToken {}", bearerToken);
            String token = jwtUtils.getTokenFromBearerToken(bearerToken);
            log.info("-------> token {}", token);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            log.info("-------> username {}", username);
            response =commentsService.deleteCommentById(dto.getId() ,username ) ;

            return response;
        } catch (Exception e) {
            response.setEcode(EcodeConstant.EXCEPTION);
            response.setMessage(EcodeConstant.EXCEPTION_MSG);
            return response;
        }
    }
}
