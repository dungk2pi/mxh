package com.social.demo.services.impl;

import java.util.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.AddFriend;
import com.social.demo.models.Notifications;
import com.social.demo.models.UserSetting;
import com.social.demo.models.Users;
import com.social.demo.payload.request.AddFriendRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.AddFriendRepository;
import com.social.demo.repository.NotificationRepository;
import com.social.demo.repository.UserSettingRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.AddFriendService;
import com.social.demo.services.FirebaseMessageService;

@Service
public class AddFriendServiceImpl implements AddFriendService {
  private static final Logger log = LogManager.getLogger(AddFriendServiceImpl.class);

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private AddFriendRepository addFriendRepository;

  @Autowired
  private UserSettingRepository userSettingRepository;

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private FirebaseMessageService firebaseMessageService;

  @Override
  public CommonResponse<Object> requestAddFriend(AddFriendRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      final String addFriendId = "addFriend_" + UUID.randomUUID().toString().replace("-", "");

      Optional<UserSetting> userRequestFr = userSettingRepository.findByUserId(onlineUser.get().getId());

      Optional<UserSetting> userRecieveFr = userSettingRepository.findByUserId(request.getUserRevId());

      if(userRequestFr.get().getListBlockAll().contains(request.getUserRevId())
      || userRecieveFr.get().getDisableTag() == 1
      || userRecieveFr.get().getListBlockAll().contains(onlineUser.get().getId())
      || onlineUser.get().getListFriend().contains(request.getUserRevId())
      ){
        response.setEcode(EcodeConstant.NOT_ACCESS);
        response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
        return response;
      }else{
        if(request.getFlag().equals("add")){
          Optional<AddFriend> _addFriend = addFriendRepository.findByUserReqIdAndUserRevIdOrUserReqIdAndUserRevId(onlineUser.get().getId(),
          request.getUserRevId(), request.getUserRevId(), onlineUser.get().getId());

          if(_addFriend.isPresent()){
            log.error("Request friend already exist");
            response.setEcode(EcodeConstant.EXIST);
            response.setMessage(EcodeConstant.EXIST_MSG);
            return response;
          }else{
            AddFriend _addFr = new AddFriend();

            _addFr.setId(addFriendId);
            _addFr.setUserReqId(onlineUser.get().getId());
            _addFr.setUserRevId(request.getUserRevId());

            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR, 7);
            Date now = c.getTime();
            _addFr.setCreateDate(now);

            //push noti and save here
            Notifications _notifications  = new Notifications();;
            final String notificationId = "notification_" + UUID.randomUUID().toString().replace("-", "");

            _notifications.setId(notificationId);
            _notifications.setUserReqId(onlineUser.get().getId());
            _notifications.setUserRevId(request.getUserRevId());
            _notifications.setReferenceId(addFriendId);

            firebaseMessageService.sendNotificationByToken("ftmhLfslTlG9-VD3W8Ag7z:APA91bHgsbx5ejIPKumtPOz7JveyEcOtAU6d0gOzhf8985uavWnIYS3NOuHN1AOFPDM-yN81qiGkS4jF5sEv6C3Mry43uhYpRbZtxsYFVrI-_DRdVa062Vo9LRn_SBmBM8A3y6Xhutui",
            onlineUser.get().getUsername() + " đã gửi lời mời kết bạn", "", onlineUser.get().getAvatar());

            _notifications.setContent(" đã gửi lời mời kết bạn");
            _notifications.setRead(false);
            _notifications.setStatus(1);

            Calendar cNoti = Calendar.getInstance();
            cNoti.add(Calendar.HOUR, 7);
            Date nowNoti = cNoti.getTime();
            _notifications.setCreateDate(nowNoti);
            _notifications.setEditDate(nowNoti);

            notificationRepository.save(_notifications);
            addFriendRepository.save(_addFr);

            response.setData(addFriendId);
            response.setEcode(EcodeConstant.SUCCESS);
            response.setMessage(EcodeConstant.SUCCESS_MSG);

            return response;
          }
        }else{
          if(request.getFlag().equals("remove")){
            Optional<AddFriend> _addFriend = addFriendRepository.findByUserReqIdAndUserRevId(onlineUser.get().getId(), request.getUserRevId());

            if(_addFriend.isEmpty()){
              log.error("Request friend not exist");
              response.setEcode(EcodeConstant.NULL);
              response.setMessage(EcodeConstant.NULL_MSG);
              return response;
            }else{
              log.info("delete request add friend by id {}", _addFriend.get().getId());
              addFriendRepository.deleteById(_addFriend.get().getId());

              response.setData(EcodeConstant.SUCCESS);
              response.setMessage(EcodeConstant.SUCCESS_MSG);
              return response;
            }
          }else{
            response.setEcode(EcodeConstant.REQ_ERROR);
            response.setMessage(EcodeConstant.REQ_ERROR_MSG);
            return response;
          }
        }
      }
    } catch (Exception e) {
      log.error("Exception during request add friend {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> handleAddFriend(AddFriendRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      Optional<UserSetting> userRequestFr = userSettingRepository.findByUserId(request.getUserRevId());

      Optional<UserSetting> userRecieveFr = userSettingRepository.findByUserId(onlineUser.get().getId());

      if(userRequestFr.get().getListBlockAll().contains(onlineUser.get().getId())
      || userRecieveFr.get().getListBlockAll().contains(request.getUserRevId())){
        response.setEcode(EcodeConstant.NOT_ACCESS);
        response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
        return response;
      }else{
        if(request.getFlag().equals("accept")){
          Optional<AddFriend> _addFriend = addFriendRepository.findByUserReqIdAndUserRevId(request.getUserRevId(), onlineUser.get().getId());

          if(_addFriend.isEmpty()){
              log.error("Request friend not exist");
              response.setEcode(EcodeConstant.NULL);
              response.setMessage(EcodeConstant.NULL_MSG);
              return response;
          }else{
              log.info("accept request add friend by id {}", _addFriend.get().getId());
              addFriendRepository.deleteById(_addFriend.get().getId());

              Calendar c = Calendar.getInstance();
              c.add(Calendar.HOUR, 7);
              Date now = c.getTime();

              Optional<Users> _userReqAddFr = usersRepository.findById(request.getUserRevId());
              if (_userReqAddFr.isPresent()) {
                Users user = _userReqAddFr.get();
                if (user.getListFriend() == null) {
                    user.setListFriend(new ArrayList<>());
                }
                user.getListFriend().add(onlineUser.get().getId());
                user.setUpdateTime(now);

                usersRepository.save(user);
              }

              Optional<Users> _userRevAddFr = usersRepository.findById(onlineUser.get().getId());
              if(_userRevAddFr.isPresent()){
                Users user = _userRevAddFr.get();
                if(user.getListFriend() == null){
                  user.setListFriend(new ArrayList<>());
                }
                user.getListFriend().add(request.getUserRevId());
                user.setUpdateTime(now);

                usersRepository.save(user);
              }

              //push noti and save here
              Notifications _notifications  = new Notifications();
              final String notificationId = "notification_" + UUID.randomUUID().toString().replace("-", "");
              _notifications.setId(notificationId);
              _notifications.setUserReqId(onlineUser.get().getId());
              _notifications.setUserRevId(request.getUserRevId());

              firebaseMessageService.sendNotificationByToken("ftmhLfslTlG9-VD3W8Ag7z:APA91bHgsbx5ejIPKumtPOz7JveyEcOtAU6d0gOzhf8985uavWnIYS3NOuHN1AOFPDM-yN81qiGkS4jF5sEv6C3Mry43uhYpRbZtxsYFVrI-_DRdVa062Vo9LRn_SBmBM8A3y6Xhutui",
              onlineUser.get().getUsername() + " đã đồng ý lời mời kết bạn", "", onlineUser.get().getAvatar());

              _notifications.setContent(" đã đồng ý lời mời kết bạn");
              _notifications.setRead(false);
              _notifications.setStatus(1);

              Calendar cNoti = Calendar.getInstance();
              cNoti.add(Calendar.HOUR, 7);
              Date nowNoti = cNoti.getTime();
              _notifications.setCreateDate(nowNoti);
              _notifications.setEditDate(nowNoti);

              notificationRepository.save(_notifications);

              response.setData(EcodeConstant.SUCCESS);
              response.setMessage(EcodeConstant.SUCCESS_MSG);
              return response;
          }
        }else{
          if(request.getFlag().equals("reject")){
            Optional<AddFriend> _addFriend = addFriendRepository.findByUserReqIdAndUserRevId(request.getUserRevId(), onlineUser.get().getId());

            if(_addFriend.isEmpty()){
              log.error("Request friend not exist");
              response.setEcode(EcodeConstant.NULL);
              response.setMessage(EcodeConstant.NULL_MSG);
              return response;
            }else{
              log.info("delete request add friend by id {}", _addFriend.get().getId());
              addFriendRepository.deleteById(_addFriend.get().getId());

              response.setData(EcodeConstant.SUCCESS);
              response.setMessage(EcodeConstant.SUCCESS_MSG);
              return response;
            }
          }else{
            response.setEcode(EcodeConstant.REQ_ERROR);
            response.setMessage(EcodeConstant.REQ_ERROR_MSG);
            return response;
          }
        }
      }
    } catch (Exception e) {
      log.error("Exception during handle request add friend {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> unFriend(AddFriendRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);
      Optional<Users> userFriend = usersRepository.findById(request.getUserRevId());

      if(onlineUser.isPresent()){
        if(!onlineUser.get().getListFriend().contains(request.getUserRevId()) || onlineUser.get().getListFriend() == null){
          response.setEcode(EcodeConstant.NULL);
          response.setMessage(EcodeConstant.NULL_MSG);
          return response;
        }else{
          onlineUser.get().getListFriend().remove(request.getUserRevId());
          userFriend.get().getListFriend().remove(onlineUser.get().getId());
          usersRepository.save(onlineUser.get());
          usersRepository.save(userFriend.get());

          log.info("Unfriend success");
          response.setEcode(EcodeConstant.SUCCESS);
          response.setMessage(EcodeConstant.SUCCESS_MSG);
        }
      }


      return response;
    } catch (Exception e) {
      log.error("Exception during un friend {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> listFriend(AddFriendRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      Optional<Users> userOwnFriend = usersRepository.findById(request.getUserOwnId());

      if(userOwnFriend.isEmpty()){
        response.setEcode(EcodeConstant.NULL);
        response.setMessage(EcodeConstant.NULL_MSG);
        return response;
      }
      if(request.getUserOwnId().equals(onlineUser.get().getId())){
      ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

      for(String itemId : userOwnFriend.get().getListFriend()){
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        Optional<Users> userFriend = usersRepository.findById(itemId);

        data.put("id", userFriend.get().getId());
        data.put("avatar", userFriend.get().getAvatar());
        data.put("username", userFriend.get().getUsername());
        data.put("fullname", userFriend.get().getFirstName() + " " + userFriend.get().getLastName());

        newArr.add(data);
      }

      response.setData(newArr);
      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);

      return response;
      }else{
        Optional<UserSetting> onlineUserSetting = userSettingRepository.findByUserId(onlineUser.get().getId());
        Optional<UserSetting> userOwnFriendSetting = userSettingRepository.findByUserId(userOwnFriend.get().getId());

        if(onlineUserSetting.get().getListBlockAll().contains(userOwnFriend.get().getId())
          || userOwnFriendSetting.get().getListBlockAll().contains(onlineUser.get().getId())){
          response.setEcode(EcodeConstant.NOT_ACCESS);
          response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
          return response;
        }

        ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

        for(String itemId : userOwnFriend.get().getListFriend()){
          LinkedHashMap<String, Object> data = new LinkedHashMap<>();

          Optional<Users> userFriend = usersRepository.findById(itemId);

          data.put("id", userFriend.get().getId());
          data.put("avatar", userFriend.get().getAvatar());
          data.put("username", userFriend.get().getUsername());
          data.put("fullname", userFriend.get().getFirstName() + " " + userFriend.get().getLastName());

          newArr.add(data);
        }

        response.setData(newArr);
        response.setEcode(EcodeConstant.SUCCESS);
        response.setMessage(EcodeConstant.SUCCESS_MSG);

        return response;
      }

    } catch (Exception e) {
      log.error("Exception during get list friend {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> listFriendReq(AddFriendRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      List<AddFriend> _listAddFriendReq = addFriendRepository.findByUserRevId(onlineUser.get().getId());

      ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

      for(AddFriend item : _listAddFriendReq){
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        Optional<Users> userReqAddFriend = usersRepository.findById(item.getUserReqId());
        data.put("id", userReqAddFriend.get().getId());
        data.put("avatar", userReqAddFriend.get().getAvatar());
        data.put("username", userReqAddFriend.get().getUsername());
        data.put("fullname", userReqAddFriend.get().getFirstName() + " " + userReqAddFriend.get().getLastName());

        newArr.add(data);
      }
      response.setData(newArr);
      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);

      return response;
    } catch (Exception e) {
      log.error("Exception during get list friend {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

}
