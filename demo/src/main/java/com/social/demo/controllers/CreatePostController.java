package com.social.demo.controllers;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.payload.request.CreatePostRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.security.jwt.JwtUtils;
import com.social.demo.services.CreatePostService;

@RequestMapping("/api/func/post")
@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = "*", maxAge = 3600)
public class CreatePostController {
  private static final Logger log = LogManager.getLogger(CreatePostController.class);


	@Autowired
	private JwtUtils jwtUtils;

  @Autowired
  private CreatePostService createPostService;


  @PostMapping("/createPost")
  public CommonResponse<Object> createNewPost(@RequestBody CreatePostRequest request, @RequestHeader Map<String, String> header){
    CommonResponse<Object> response = new CommonResponse<Object>();
      try {
        String bearerToken = header.get("authorization");
  			log.info("-------> bearerToken {}", bearerToken);
        String token = jwtUtils.getTokenFromBearerToken(bearerToken);
  			log.info("-------> token {}", token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
  			log.info("-------> username {}", username);

        response = createPostService.createNewPost(request, username);

        return response;
      } catch (Exception e) {
        response.setEcode(EcodeConstant.EXCEPTION);
        response.setMessage(EcodeConstant.EXCEPTION_MSG);
        return response;
      }
	}
}
