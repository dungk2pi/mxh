package com.social.demo.controllers;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.payload.request.HiddenRequest;
import com.social.demo.payload.request.IdRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.security.jwt.JwtUtils;
import com.social.demo.services.PostsService;
import jakarta.validation.constraints.NotBlank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/func/post")
@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = "*", maxAge = 3600)
public class DeletePostController {
    private final Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PostsService postsService;

    @DeleteMapping("/deletePostById")
    public CommonResponse<Object> deleteCommentById(@RequestBody IdRequest idRequest, @RequestHeader Map<String, String> header){
        CommonResponse<Object> response = new CommonResponse<Object>();
        try {
            String bearerToken = header.get("authorization");
            log.info("-------> bearerToken {}", bearerToken);
            String token = jwtUtils.getTokenFromBearerToken(bearerToken);
            log.info("-------> token {}", token);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            log.info("-------> username {}", username);
            response = postsService.deletePostById(idRequest.getId() ,username ) ;
            return response;
        } catch (Exception e) {
            response.setEcode(EcodeConstant.EXCEPTION);
            response.setMessage(EcodeConstant.EXCEPTION_MSG);
            return response;
        }
    }

}
