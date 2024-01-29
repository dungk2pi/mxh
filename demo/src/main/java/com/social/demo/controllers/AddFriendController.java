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
import com.social.demo.payload.request.AddFriendRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.security.jwt.JwtUtils;
import com.social.demo.services.AddFriendService;

@RequestMapping("/api/func/friend")
@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = "*", maxAge = 3600)
public class AddFriendController {
  private static final Logger log = LogManager.getLogger(AddFriendController.class);

  @Autowired
	private JwtUtils jwtUtils;

  @Autowired
  private AddFriendService addFriendService;

  @PostMapping("/addFriend")
  public CommonResponse<Object> addFriend(@RequestBody AddFriendRequest request, @RequestHeader Map<String, String> header){
    CommonResponse<Object> response = new CommonResponse<Object>();
      try {
        String bearerToken = header.get("authorization");
  			log.info("-------> bearerToken {}", bearerToken);
        String token = jwtUtils.getTokenFromBearerToken(bearerToken);
  			log.info("-------> token {}", token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
  			log.info("-------> username {}", username);

        response = addFriendService.requestAddFriend(request, username);

        return response;
      } catch (Exception e) {
        response.setEcode(EcodeConstant.EXCEPTION);
        response.setMessage(EcodeConstant.EXCEPTION_MSG);
        return response;
      }
	}


  @PostMapping("/handleAddFriend")
  public CommonResponse<Object> handleAddFriend(@RequestBody AddFriendRequest request, @RequestHeader Map<String, String> header){
    CommonResponse<Object> response = new CommonResponse<Object>();
      try {
        String bearerToken = header.get("authorization");
  			log.info("-------> bearerToken {}", bearerToken);
        String token = jwtUtils.getTokenFromBearerToken(bearerToken);
  			log.info("-------> token {}", token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
  			log.info("-------> username {}", username);

        response = addFriendService.handleAddFriend(request, username);

        return response;
      } catch (Exception e) {
        response.setEcode(EcodeConstant.EXCEPTION);
        response.setMessage(EcodeConstant.EXCEPTION_MSG);
        return response;
      }
	}


  @PostMapping("/unFriend")
  public CommonResponse<Object> unFriend(@RequestBody AddFriendRequest request, @RequestHeader Map<String, String> header){
    CommonResponse<Object> response = new CommonResponse<Object>();
      try {
        String bearerToken = header.get("authorization");
  			log.info("-------> bearerToken {}", bearerToken);
        String token = jwtUtils.getTokenFromBearerToken(bearerToken);
  			log.info("-------> token {}", token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
  			log.info("-------> username {}", username);

        response = addFriendService.unFriend(request, username);

        return response;
      } catch (Exception e) {
        response.setEcode(EcodeConstant.EXCEPTION);
        response.setMessage(EcodeConstant.EXCEPTION_MSG);
        return response;
      }
	}

  @PostMapping("/listFriendReq")
  public CommonResponse<Object> listFriendReq(@RequestBody AddFriendRequest request, @RequestHeader Map<String, String> header){
    CommonResponse<Object> response = new CommonResponse<Object>();
      try {
        String bearerToken = header.get("authorization");
  			log.info("-------> bearerToken {}", bearerToken);
        String token = jwtUtils.getTokenFromBearerToken(bearerToken);
  			log.info("-------> token {}", token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
  			log.info("-------> username {}", username);

        response = addFriendService.listFriendReq(request, username);

        return response;
      } catch (Exception e) {
        response.setEcode(EcodeConstant.EXCEPTION);
        response.setMessage(EcodeConstant.EXCEPTION_MSG);
        return response;
      }
	}

  @PostMapping("/listFriend")
  public CommonResponse<Object> listFriend(@RequestBody AddFriendRequest request, @RequestHeader Map<String, String> header){
    CommonResponse<Object> response = new CommonResponse<Object>();
      try {
        String bearerToken = header.get("authorization");
  			log.info("-------> bearerToken {}", bearerToken);
        String token = jwtUtils.getTokenFromBearerToken(bearerToken);
  			log.info("-------> token {}", token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
  			log.info("-------> username {}", username);

        response = addFriendService.listFriend(request, username);

        return response;
      } catch (Exception e) {
        response.setEcode(EcodeConstant.EXCEPTION);
        response.setMessage(EcodeConstant.EXCEPTION_MSG);
        return response;
      }
	}
}
