package com.social.demo.services.impl;

import java.util.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.Notifications;
import com.social.demo.models.Posts;
import com.social.demo.models.Reacts;
import com.social.demo.models.UserSetting;
import com.social.demo.models.Users;
import com.social.demo.payload.request.CreateReactRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.NotificationRepository;
import com.social.demo.repository.PostsRepository;
import com.social.demo.repository.ReactRepository;
import com.social.demo.repository.UserSettingRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.CreateReactService;
import com.social.demo.services.FirebaseMessageService;

@Service
public class CreateReactServiceImpl implements CreateReactService {
  private static final Logger log = LogManager.getLogger(CreateReactServiceImpl.class);

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private ReactRepository reactRepository;

  @Autowired
  private PostsRepository postsRepository;

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private UserSettingRepository userSettingRepository;

  @Autowired
  private FirebaseMessageService firebaseMessageService;

  @Override
  public CommonResponse<Object> createReact(CreateReactRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();


    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);
      final String reactId = "react_" + UUID.randomUUID().toString().replace("-", "");
      Optional<Reacts> react = reactRepository.findByReferenceIdAndUserId(request.getReferenceId(),onlineUser.get().getId());
      Optional<Posts> post = postsRepository.findById(request.getPostId());

      Optional<Users> authPost = usersRepository.findById(post.get().getUserId());

      Optional<UserSetting> authSettingPost = userSettingRepository.findByUserId(authPost.get().getId());

      if(authSettingPost.isEmpty() || !authSettingPost.get().getListBlockAll().contains(onlineUser.get().getId())){
        if(request.getStatus() == 1){
          Reacts _react;
          if(react.isPresent()){
            _react = react.get();
            _react.setType(request.getType());

            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR, 7);
            Date now = c.getTime();
            _react.setUpdateDate(now);
            reactRepository.save(_react);
          }else{
            _react = new Reacts();
            _react.setId(reactId);
            _react.setStatus(1);
            _react.setReferenceId(request.getReferenceId());
            _react.setUserId(onlineUser.get().getId());
            _react.setType(request.getType());

            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR, 7);
            Date now = c.getTime();
            _react.setCreateDate(now);
            _react.setUpdateDate(now);

            reactRepository.save(_react);


            if(post.isEmpty()){
              log.error("bài post không tồn tại");
              response.setEcode(EcodeConstant.NULL);
              response.setMessage(EcodeConstant.NULL_MSG);
              return response;
            }else{
              post.get().setLastReactDate(now);
              postsRepository.save(post.get());
            }

            Notifications _notifications;
            final String notificationId = "notification_" + UUID.randomUUID().toString().replace("-", "");

            _notifications = new Notifications();

            _notifications.setId(notificationId);
            _notifications.setUserReqId(onlineUser.get().getId());
            _notifications.setUserRevId(post.get().getUserId());
            _notifications.setReferenceId(post.get().getId());

            if(post.get().getId().equals(request.getReferenceId())){
              firebaseMessageService.sendNotificationByToken("ftmhLfslTlG9-VD3W8Ag7z:APA91bHgsbx5ejIPKumtPOz7JveyEcOtAU6d0gOzhf8985uavWnIYS3NOuHN1AOFPDM-yN81qiGkS4jF5sEv6C3Mry43uhYpRbZtxsYFVrI-_DRdVa062Vo9LRn_SBmBM8A3y6Xhutui",
              onlineUser.get().getUsername() + " đã bài tỏ cảm xúc về bài viết của bạn", "", onlineUser.get().getAvatar());

              _notifications.setContent("đã bày tỏ cảm xúc về bài viết của bạn");

            }else{
              firebaseMessageService.sendNotificationByToken("ftmhLfslTlG9-VD3W8Ag7z:APA91bHgsbx5ejIPKumtPOz7JveyEcOtAU6d0gOzhf8985uavWnIYS3NOuHN1AOFPDM-yN81qiGkS4jF5sEv6C3Mry43uhYpRbZtxsYFVrI-_DRdVa062Vo9LRn_SBmBM8A3y6Xhutui",
              onlineUser.get().getUsername() + " đã bày tỏ cảm xúc về bình luận của bạn", "", onlineUser.get().getAvatar());

              _notifications.setContent("đã bày tỏ cảm xúc về bình luận của bạn");
            }

            _notifications.setRead(false);
            _notifications.setStatus(1);

            Calendar cNoti = Calendar.getInstance();
            cNoti.add(Calendar.HOUR, 7);
            Date nowNoti = cNoti.getTime();
            _notifications.setCreateDate(nowNoti);
            _notifications.setEditDate(nowNoti);

            notificationRepository.save(_notifications);
          }
        }else{
          if(react.isPresent()){
            log.info("XOA BAN GHI REACT CO ID: " + react.get().getId());
            reactRepository.deleteById(react.get().getId());
          }else{
            log.info("KHÔNG TỒN TẠI BẢN GHI CẦN XÓA: " + react.get().getId());
          }
        }
      }else{
        response.setEcode(EcodeConstant.NOT_ACCESS);
        response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
        return response;
      }

      response.setData(reactId);
      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);
      return response;
    } catch (Exception e) {
      log.error("Exception during create react {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }
}
