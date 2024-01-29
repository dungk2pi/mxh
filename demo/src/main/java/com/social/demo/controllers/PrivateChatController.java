package com.social.demo.controllers;

import java.util.Map;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.ChatMessage;
import com.social.demo.payload.request.CreatePostRequest;
import com.social.demo.payload.request.PrivateChatRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.security.jwt.JwtUtils;
import com.social.demo.services.PrivateChatService;

@RestController
@RequestMapping("/api/func/chat")
@EnableAutoConfiguration
@CrossOrigin(origins = "*", maxAge = 3600)
public class PrivateChatController {
  private static final Logger log = LogManager.getLogger(AddFriendController.class);

  @Autowired
	private JwtUtils jwtUtils;

  @Autowired
  private PrivateChatService privateChatService;


  @MessageMapping("/sendPrivateMsg")
  @SendTo("/topic/public")
  public PrivateChatRequest sendMessage(@Payload PrivateChatRequest request, @Headers Map<String, Object> headers) {
    try {

      Object nativeHeadersObject = headers.get("nativeHeaders");

      Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) nativeHeadersObject;

      List<String> authorizationHeaders = nativeHeaders.get("Authorization");
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
          String bearerToken = authorizationHeaders.get(0);
          log.info("-------> bearer token {}", bearerToken);
          String token = jwtUtils.getTokenFromBearerToken(bearerToken);
  			  log.info("-------> token {}", token);
          String username = jwtUtils.getUserNameFromJwtToken(token);
  			  log.info("-------> username {}", username);

          privateChatService.sendMessage(request, username);
          }
      return request;
    } catch (Exception e) {
      return null;
    }
  }

  @PostMapping("/chatDetail")
  public CommonResponse<Object> getChatDetail(@RequestBody PrivateChatRequest request, @RequestHeader Map<String, String> header){
    CommonResponse<Object> response = new CommonResponse<Object>();
      try {
        String bearerToken = header.get("authorization");
  			log.info("-------> bearerToken {}", bearerToken);
        String token = jwtUtils.getTokenFromBearerToken(bearerToken);
  			log.info("-------> token {}", token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
  			log.info("-------> username {}", username);

        response = privateChatService.getChatDetail(request, username);

        return response;
      } catch (Exception e) {
        response.setEcode(EcodeConstant.EXCEPTION);
        response.setMessage(EcodeConstant.EXCEPTION_MSG);
        return response;
      }
	}


  @MessageMapping("/chat.addUser")
  @SendTo("/topic/public")
  public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
      // Add username in web socket session
      headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
      return chatMessage;
  }
}
