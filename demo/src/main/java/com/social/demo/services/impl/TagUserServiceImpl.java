package com.social.demo.services.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.UserSetting;
import com.social.demo.models.Users;
import com.social.demo.payload.request.TagUserRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.UserSettingRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.TagUserService;
import com.social.demo.utils.Utils;

@Service
public class TagUserServiceImpl implements TagUserService {
  private static final Logger log = LogManager.getLogger(TagUserServiceImpl.class);

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private UserSettingRepository userSettingRepository;

  @Override
  public CommonResponse<Object> tagUser(TagUserRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();
    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);


      List<Users> _user = new ArrayList<Users>();
      Page<Users> pageUsers;

      Pageable paging = PageRequest.of(request.getPage(), 10, Sort.by("name"));

      ArrayList<LinkedHashMap<String, String>> newArr = new ArrayList<>();

      if(request.getName() == null || request.getName().equals("")){
        //FE truyền lên chuỗi rỗng hoặc null
        pageUsers = usersRepository.findByUsernameContainingIgnoreCase("", paging);
        _user = pageUsers.getContent();
        log.info("input name = rong, so luong user tim thay: " + _user.size());
        for(Users item : _user){
          LinkedHashMap<String, String> data = new LinkedHashMap<>();

          Optional<UserSetting> _userSetting = userSettingRepository.findByUserId(item.getId());

          if(_userSetting.isEmpty() || _userSetting.get().getDisableTag() == 0
          || !_userSetting.get().getListBlockAll().contains(onlineUser.get().getId()) ){
            data.put("id", item.getId());
            data.put("username", item.getUsername());
            data.put("avatar", item.getAvatar());
            data.put("nickname", item.getNickname());
            newArr.add(data);
          }
        }
      }else{
        String nameAccented = Utils.removeAccent(request.getName());
        log.info("name accented {}", nameAccented);
        //FE truyền lên chuỗi rỗng hoặc null
        if(!nameAccented.equals(request.getName())){
          pageUsers = usersRepository.findByUsernameContainingIgnoreCase(request.getName(), paging);
          _user = pageUsers.getContent();
          log.info("input name co dau, so luong user tim thay: " + _user.size());
          for(Users item : _user){
            LinkedHashMap<String, String> data = new LinkedHashMap<>();

            Optional<UserSetting> _userSetting = userSettingRepository.findByUserId(item.getId());

            if(_userSetting.isEmpty() || _userSetting.get().getDisableTag() == 0
          || !_userSetting.get().getListBlockAll().contains(onlineUser.get().getId()) ){
              data.put("id", item.getId());
              data.put("username", item.getUsername());
              data.put("avatar", item.getAvatar());
              data.put("nickname", item.getNickname());
              newArr.add(data);
            }
          }
        }else{
           pageUsers = usersRepository.findByUsernameContainingIgnoreCase(nameAccented, paging);
          _user = pageUsers.getContent();
          log.info("input name ko dau, so luong user tim thay: " + _user.size());
          for(Users item : _user){
            LinkedHashMap<String, String> data = new LinkedHashMap<>();

            Optional<UserSetting> _userSetting = userSettingRepository.findByUserId(item.getId());

            if(_userSetting.isEmpty() || _userSetting.get().getDisableTag() == 0
          || !_userSetting.get().getListBlockAll().contains(onlineUser.get().getId()) ){
            data.put("id", item.getId());
            data.put("username", item.getUsername());
            data.put("avatar", item.getAvatar());
            data.put("nickname", item.getNickname());
            newArr.add(data);
            }
          }
        }
      }

      if(_user.isEmpty()){
        response.setData(newArr);
      }else{
        log.info("danh sach user {}", _user.toString());
        response.setData(newArr);
      }
      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);
      return response;
    } catch (Exception e) {
      log.error("Exception during tag user {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

}
