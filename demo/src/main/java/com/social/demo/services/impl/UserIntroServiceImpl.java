package com.social.demo.services.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.UserSetting;
import com.social.demo.models.Users;
import com.social.demo.payload.request.UserIntroRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.UserSettingRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.UserIntroService;

@Service
public class UserIntroServiceImpl implements UserIntroService {
  private static final Logger log = LogManager.getLogger(UserIntroServiceImpl.class);


  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private UserSettingRepository userSettingRepository;

  @Override
  public CommonResponse<Object> getUserIntro(UserIntroRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);
      Optional<Users> requestUser = usersRepository.findById(request.getUserId());


      LinkedHashMap<String, Object> data = new LinkedHashMap<>();

      if(requestUser.isEmpty()){
        response.setEcode(EcodeConstant.NULL);
        response.setMessage(EcodeConstant.NULL_MSG);
        return response;
      }

      if(request.getUserId().equals(onlineUser.get().getId())){
        Optional<UserSetting> onlineUserSetting = userSettingRepository.findByUserId(onlineUser.get().getId());

        data.put("isOnwer", true);
        HashMap<String, String> itemData = new LinkedHashMap<>();
        itemData.put("item", onlineUser.get().getFirstName() + " " + onlineUser.get().getLastName());
        itemData.put("isPublic", Boolean.toString(onlineUserSetting.get().isPubFullname()));
        data.put("fullname", itemData);


        itemData = new HashMap<>();
        itemData.put("item", onlineUser.get().getEmail());
        itemData.put("isPublic", Boolean.toString(onlineUserSetting.get().isPubEmail()));
        data.put("email", itemData);

        itemData = new HashMap<>();
        itemData.put("item", onlineUser.get().getPhone());
        itemData.put("isPublic", Boolean.toString(onlineUserSetting.get().isPubPhone()));
        data.put("phone", itemData);

        itemData = new HashMap<>();
        itemData.put("item", onlineUser.get().getWorkpalace());
        itemData.put("isPublic", Boolean.toString(onlineUserSetting.get().isPubWorkpalace()));
        data.put("workPalace", itemData);

        itemData = new HashMap<>();
        itemData.put("item", onlineUser.get().getDateOfBirth() != null ? onlineUser.get().getDateOfBirth().toString() : null);
        itemData.put("isPublic", Boolean.toString(onlineUserSetting.get().isPubDateOfBirth()));
        data.put("dateOfBirth", itemData);

        itemData = new HashMap<>();
        itemData.put("item", onlineUser.get().getAddress());
        itemData.put("isPublic", Boolean.toString(onlineUserSetting.get().isPubAddress()));
        data.put("address", itemData);

        response.setData(data);
        response.setEcode(EcodeConstant.SUCCESS);
        response.setMessage(EcodeConstant.SUCCESS_MSG);
        log.info("get success introduction from user{}", onlineUser.get().getId())  ;
        return response;
      }else{
        Optional<UserSetting> requestUserSetting = userSettingRepository.findByUserId(requestUser.get().getId());
        data.put("isOnwer", false);
        HashMap<String, String> itemData = new LinkedHashMap<>();
        if(requestUserSetting.get().isPubFullname()){
          itemData.put("item", requestUser.get().getFirstName() + " " + requestUser.get().getLastName());
          itemData.put("isPublic", Boolean.toString(requestUserSetting.get().isPubFullname()));
          data.put("fullname", itemData);
        }

        if(requestUserSetting.get().isPubEmail()){
          itemData = new HashMap<>();
          itemData.put("item", requestUser.get().getEmail());
          itemData.put("isPublic", Boolean.toString(requestUserSetting.get().isPubEmail()));
          data.put("email", itemData);
        }


        if(requestUserSetting.get().isPubPhone()){
          itemData = new HashMap<>();
          itemData.put("item", requestUser.get().getPhone());
          itemData.put("isPublic", Boolean.toString(requestUserSetting.get().isPubPhone()));
          data.put("phone", itemData);
        }

        if(requestUserSetting.get().isPubWorkpalace()){
          itemData = new HashMap<>();
          itemData.put("item", requestUser.get().getWorkpalace());
          itemData.put("isPublic", Boolean.toString(requestUserSetting.get().isPubWorkpalace()));
          data.put("workPalace", itemData);
        }

        if(requestUserSetting.get().isPubDateOfBirth()){
          itemData = new HashMap<>();
          itemData.put("item", requestUser.get().getDateOfBirth() != null ? requestUser.get().getDateOfBirth().toString() : null);
          itemData.put("isPublic", Boolean.toString(requestUserSetting.get().isPubDateOfBirth()));
          data.put("dateOfBirth", itemData);
        }

        if(requestUserSetting.get().isPubAddress()){
          itemData = new HashMap<>();
          itemData.put("item", requestUser.get().getAddress());
          itemData.put("isPublic", Boolean.toString(requestUserSetting.get().isPubAddress()));
          data.put("address", itemData);
        }

        response.setData(data);
        response.setEcode(EcodeConstant.SUCCESS);
        response.setMessage(EcodeConstant.SUCCESS_MSG);
        return response;
      }

    } catch (Exception e) {
      log.error("Exception during get user introduction {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

}
