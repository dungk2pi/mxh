package com.social.demo.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.PrivateChat;
import com.social.demo.models.Users;
import com.social.demo.payload.request.PrivateChatRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.PrivateChatRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.PrivateChatService;
import com.social.demo.utils.Utils;

@Service
public class PrivateChatServiceImpl implements PrivateChatService {
  private static final Logger log = LogManager.getLogger(PrivateChatServiceImpl.class);


  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private PrivateChatRepository privateChatRepository;

  @Override
  public CommonResponse<Object> getChatDetail(PrivateChatRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      String mergeId = Utils.getConversation(onlineUser.get().getId(), request.getToId());

      List<PrivateChat> _privateChats = new ArrayList<PrivateChat>();

      Page<PrivateChat> pagePrivateChat;

      Pageable paging = PageRequest.of(request.getPage(), 5);

      pagePrivateChat = privateChatRepository.findByMergeIdOrderBySentDateDesc(mergeId, paging);

      _privateChats = pagePrivateChat.getContent();

      log.info("private chat detail size {}", _privateChats.size());

      ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

      for(PrivateChat item : _privateChats){
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        data.put("msg", item.getMsg());
        data.put("type", item.getType());
        data.put("fromId", item.getFromId());
        data.put("toId", item.getToId());
        data.put("sentDate", item.getSentDate());

        newArr.add(data);
      }
      response.setData(newArr);
      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);

      return response;
    } catch (Exception e) {
      log.error("Exception during private chat {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> sendMessage(PrivateChatRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      String mergeId = Utils.getConversation(onlineUser.get().getId(), request.getToId());

      PrivateChat _privateChat = new PrivateChat();

      _privateChat.setMergeId(mergeId);
      _privateChat.setFromId(onlineUser.get().getId());
      _privateChat.setToId(request.getToId());
      _privateChat.setMsg(request.getMsg());
      _privateChat.setType(request.getType());

      Calendar c = Calendar.getInstance();
			c.add(Calendar.HOUR, 7);
			Date now = c.getTime();
      _privateChat.setSentDate(now);

      privateChatRepository.save(_privateChat);

      log.info("private chat id={} | Save private chat", mergeId);


      return response;
    } catch (Exception e) {
      log.error("Exception during private chat {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

}
