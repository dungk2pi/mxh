package com.social.demo.services.impl;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.Comments;
import com.social.demo.models.Media;
import com.social.demo.models.Posts;
import com.social.demo.models.Reacts;
import com.social.demo.models.UserSetting;
import com.social.demo.models.Users;
import com.social.demo.payload.request.UserInfoRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.CommentRepository;
import com.social.demo.repository.MediaRepository;
import com.social.demo.repository.PostsRepository;
import com.social.demo.repository.ReactRepository;
import com.social.demo.repository.UserSettingRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService {
  private static final Logger log = LogManager.getLogger(UserInfoServiceImpl.class);

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private PostsRepository postsRepository;

  @Autowired
  private MediaRepository mediaRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private ReactRepository reactRepository;

  @Autowired
  private UserSettingRepository userSettingRepository;

  @Override
  public CommonResponse<Object> getUserInfo(UserInfoRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      LinkedHashMap<String, Object> data = new LinkedHashMap<>();

      Optional<Users> requestUser = usersRepository.findById(request.getUserId());

      if(requestUser.isEmpty()){
        response.setEcode(EcodeConstant.NULL);
        response.setMessage(EcodeConstant.NULL_MSG);
        return response;
      }else{
        if(request.getUserId().equals(onlineUser.get().getId())){
            data.put("avatar", requestUser.get().getAvatar());
            data.put("background", requestUser.get().getBackground());
            data.put("isOwner", true);
        }else{
          Optional<UserSetting> onlineUserSetting = userSettingRepository.findByUserId(onlineUser.get().getId());
          Optional<UserSetting> requestUserSetting = userSettingRepository.findByUserId(requestUser.get().getId());

          if(onlineUserSetting.get().getListBlockAll().contains(requestUser.get().getId())
          || requestUserSetting.get().getListBlockAll().contains(onlineUser.get().getId())){
            response.setEcode(EcodeConstant.NOT_ACCESS);
            response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
            return response;
          }
            data.put("avatar", requestUser.get().getAvatar());
            data.put("background", requestUser.get().getBackground());
            data.put("isOwner", false);

        }
        response.setData(data);
        response.setEcode(EcodeConstant.SUCCESS);
        response.setMessage(EcodeConstant.SUCCESS_MSG);
        return response;
      }
    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> getUserPosts(UserInfoRequest request, String username) {
   CommonResponse<Object> response = new CommonResponse<Object>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      Optional<Users> requestUser = usersRepository.findById(request.getUserId());

      if(request.getUserId().equals(onlineUser.get().getId())){
        List<Posts> _posts = new ArrayList<Posts>();

        Page<Posts> pagePosts;

        Pageable paging = PageRequest.of(request.getPage(), 10);

        pagePosts = postsRepository.findByUserIdAndReportStatusOrderByLastReactDateDesc(onlineUser.get().getId(), "active",paging);
        _posts = pagePosts.getContent();

        log.info("post size {}", _posts.size());

        ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

        for(Posts item : _posts){
          LinkedHashMap<String, Object> data = new LinkedHashMap<>();
          List<Media> _medias = mediaRepository.findByReferenceIdOrderBySeqAsc(item.getId());
          Optional<Users> authorPost = usersRepository.findById(item.getUserId());


          data.put("postId", item.getId());
          data.put("content", item.getContent());

          if(!_medias.isEmpty()){
            ArrayList<Object> newArrMedia = new ArrayList<>();
            for(Media mediaItem : _medias){
            LinkedHashMap<String, String> mediaData = new LinkedHashMap<>();
              mediaData.put("url", mediaItem.getMediaUrl());
              mediaData.put("status", mediaItem.getStatus());
              mediaData.put("type", mediaItem.getType());
              newArrMedia.add(mediaData);
            }
            data.put("images", newArrMedia);
          }

          data.put("createDate", item.getCreateDate());

          LinkedHashMap<String, String> author = new LinkedHashMap<>();
          author.put("authorId", authorPost.get().getId());
          author.put("username", authorPost.get().getUsername());
          author.put("authorAvatar", authorPost.get().getAvatar());
          data.put("poster",author);

          List<Comments> _comments = commentRepository.findByReferenceId(item.getId());
          List<Reacts> _reacts = reactRepository.findByReferenceId(item.getId());

          Optional<Reacts> _hasReacted = reactRepository.findByReferenceIdAndUserId(item.getId(),onlineUser.get().getId());

          data.put("commentCount", _comments.size());
          data.put("likeCounts", _reacts.size());

          if(_hasReacted.isPresent()){
            data.put("hasReacted", true);
          }else{
            data.put("hasReacted", false);
          }

          data.put("status", item.getStatus());
          newArr.add(data);
        }
        response.setData(newArr);
        response.setEcode(EcodeConstant.SUCCESS);
        response.setMessage(EcodeConstant.SUCCESS_MSG);
        return response;
      }else{
        Optional<UserSetting> onlineUserSetting = userSettingRepository.findByUserId(onlineUser.get().getId());
        Optional<UserSetting> requestUserSetting = userSettingRepository.findByUserId(requestUser.get().getId());

        if(onlineUserSetting.get().getListBlockAll().contains(requestUser.get().getId())
          || requestUserSetting.get().getListBlockAll().contains(onlineUser.get().getId())){
            response.setEcode(EcodeConstant.NOT_ACCESS);
            response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
            return response;
        }
        List<Posts> _posts = new ArrayList<Posts>();

        Page<Posts> pagePosts;

        Pageable paging = PageRequest.of(request.getPage(), 10);


        pagePosts = postsRepository.findByUserIdAndStatusAndReportStatusOrderByLastReactDateDesc(request.getUserId(), "public", "active",paging);
        _posts = pagePosts.getContent();

        log.info("post size {}", _posts.size());

        ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

        for(Posts item : _posts){
          LinkedHashMap<String, Object> data = new LinkedHashMap<>();
          List<Media> _medias = mediaRepository.findByReferenceIdOrderBySeqAsc(item.getId());
          Optional<Users> authorPost = usersRepository.findById(item.getUserId());


          data.put("postId", item.getId());
          data.put("content", item.getContent());

          if(!_medias.isEmpty()){
            ArrayList<Object> newArrMedia = new ArrayList<>();
            for(Media mediaItem : _medias){
            LinkedHashMap<String, String> mediaData = new LinkedHashMap<>();
              mediaData.put("url", mediaItem.getMediaUrl());
              mediaData.put("status", mediaItem.getStatus());
              mediaData.put("type", mediaItem.getType());
              newArrMedia.add(mediaData);
            }
            data.put("images", newArrMedia);
          }
          data.put("createDate", item.getCreateDate());

          LinkedHashMap<String, String> author = new LinkedHashMap<>();
          author.put("authorId", authorPost.get().getId());
          author.put("username", authorPost.get().getUsername());
          author.put("authorAvatar", authorPost.get().getAvatar());
          data.put("poster",author);

          List<Comments> _comments = commentRepository.findByReferenceId(item.getId());
          List<Reacts> _reacts = reactRepository.findByReferenceId(item.getId());

          Optional<Reacts> _hasReacted = reactRepository.findByReferenceIdAndUserId(item.getId(),request.getUserId());

          data.put("commentCount", _comments.size());
          data.put("likeCounts", _reacts.size());

          if(_hasReacted.isPresent()){
            data.put("hasReacted", true);
          }else{
            data.put("hasReacted", false);
          }

          data.put("status", item.getStatus());
          newArr.add(data);
        }
        response.setData(newArr);
        response.setEcode(EcodeConstant.SUCCESS);
        response.setMessage(EcodeConstant.SUCCESS_MSG);
        return response;
      }

    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> getUserImages(UserInfoRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      Optional<Users> requestUser = usersRepository.findById(request.getUserId());

      if(requestUser.isEmpty()){
        response.setEcode(EcodeConstant.NULL);
        response.setMessage(EcodeConstant.NULL_MSG);
        return response;
      }else{
        if(request.getUserId().equals(onlineUser.get().getId())){
          List<Posts> _posts = new ArrayList<Posts>();

          Page<Posts> pagePosts;

          Pageable paging = PageRequest.of(request.getPage(), 10);


          pagePosts = postsRepository.findByUserIdAndReportStatusOrderByLastReactDateDesc(onlineUser.get().getId(), "active",paging);
          _posts = pagePosts.getContent();

          ArrayList<Object> newArr = new ArrayList<>();

          for(Posts item : _posts){
            List<Media> _medias = mediaRepository.findByReferenceIdAndTypeOrderBySeqAsc(item.getId(), "image");

            if(!_medias.isEmpty()){
              for(Media mediaItem : _medias){
              LinkedHashMap<String, String> mediaData = new LinkedHashMap<>();
                mediaData.put("url", mediaItem.getMediaUrl());
                mediaData.put("status", mediaItem.getStatus());
                mediaData.put("type", mediaItem.getType());
                newArr.add(mediaData);
              }
            }
          }
          response.setData(newArr);
          response.setEcode(EcodeConstant.SUCCESS);
          response.setMessage(EcodeConstant.SUCCESS_MSG);
          return response;
        }else{
          Optional<UserSetting> onlineUserSetting = userSettingRepository.findByUserId(onlineUser.get().getId());
          Optional<UserSetting> requestUserSetting = userSettingRepository.findByUserId(requestUser.get().getId());

          if(onlineUserSetting.get().getListBlockAll().contains(requestUser.get().getId())
            || requestUserSetting.get().getListBlockAll().contains(onlineUser.get().getId())){
              response.setEcode(EcodeConstant.NOT_ACCESS);
              response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
              return response;
          }

          List<Posts> _posts = new ArrayList<Posts>();
          Page<Posts> pagePosts;
          Pageable paging = PageRequest.of(request.getPage(), 10);

          pagePosts = postsRepository.findByUserIdAndStatusAndReportStatusOrderByLastReactDateDesc(request.getUserId(), "public", "active",paging);
          _posts = pagePosts.getContent();

          ArrayList<Object> newArr = new ArrayList<>();

          for(Posts item : _posts){
            List<Media> _medias = mediaRepository.findByReferenceIdAndTypeOrderBySeqAsc(item.getId(), "image");

            if(!_medias.isEmpty()){
              for(Media mediaItem : _medias){
              LinkedHashMap<String, String> mediaData = new LinkedHashMap<>();
                mediaData.put("url", mediaItem.getMediaUrl());
                mediaData.put("status", mediaItem.getStatus());
                mediaData.put("type", mediaItem.getType());
                newArr.add(mediaData);
              }
            }
          }
          response.setData(newArr);
          response.setEcode(EcodeConstant.SUCCESS);
          response.setMessage(EcodeConstant.SUCCESS_MSG);
          return response;
        }
      }

    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> getUserVideos(UserInfoRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      Optional<Users> requestUser = usersRepository.findById(request.getUserId());

      if(requestUser.isEmpty()){
        response.setEcode(EcodeConstant.NULL);
        response.setMessage(EcodeConstant.NULL_MSG);
        return response;
      }else{
        if(request.getUserId().equals(onlineUser.get().getId())){
          List<Posts> _posts = new ArrayList<Posts>();

          Page<Posts> pagePosts;

          Pageable paging = PageRequest.of(request.getPage(), 10);


          pagePosts = postsRepository.findByUserIdAndReportStatusOrderByLastReactDateDesc(onlineUser.get().getId(), "active",paging);
          _posts = pagePosts.getContent();

          ArrayList<Object> newArr = new ArrayList<>();

          for(Posts item : _posts){
            List<Media> _medias = mediaRepository.findByReferenceIdAndTypeOrderBySeqAsc(item.getId(), "video");

            if(!_medias.isEmpty()){
              for(Media mediaItem : _medias){
              LinkedHashMap<String, String> mediaData = new LinkedHashMap<>();
                mediaData.put("url", mediaItem.getMediaUrl());
                mediaData.put("status", mediaItem.getStatus());
                mediaData.put("type", mediaItem.getType());
                newArr.add(mediaData);
              }
            }
          }
          response.setData(newArr);
          response.setEcode(EcodeConstant.SUCCESS);
          response.setMessage(EcodeConstant.SUCCESS_MSG);
          return response;
        }else{
          Optional<UserSetting> onlineUserSetting = userSettingRepository.findByUserId(onlineUser.get().getId());
          Optional<UserSetting> requestUserSetting = userSettingRepository.findByUserId(requestUser.get().getId());

          if(onlineUserSetting.get().getListBlockAll().contains(requestUser.get().getId())
            || requestUserSetting.get().getListBlockAll().contains(onlineUser.get().getId())){
              response.setEcode(EcodeConstant.NOT_ACCESS);
              response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
              return response;
          }

          List<Posts> _posts = new ArrayList<Posts>();
          Page<Posts> pagePosts;
          Pageable paging = PageRequest.of(request.getPage(), 10);

          pagePosts = postsRepository.findByUserIdAndStatusAndReportStatusOrderByLastReactDateDesc(request.getUserId(), "public", "active",paging);
          _posts = pagePosts.getContent();

          ArrayList<Object> newArr = new ArrayList<>();

          for(Posts item : _posts){
            List<Media> _medias = mediaRepository.findByReferenceIdAndTypeOrderBySeqAsc(item.getId(), "video");

            if(!_medias.isEmpty()){
              for(Media mediaItem : _medias){
              LinkedHashMap<String, String> mediaData = new LinkedHashMap<>();
                mediaData.put("url", mediaItem.getMediaUrl());
                mediaData.put("status", mediaItem.getStatus());
                mediaData.put("type", mediaItem.getType());
                newArr.add(mediaData);
              }
            }
          }
          response.setData(newArr);
          response.setEcode(EcodeConstant.SUCCESS);
          response.setMessage(EcodeConstant.SUCCESS_MSG);
          return response;
        }
      }

    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> getUserSettings(UserInfoRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        if(!request.getUserId().equals(onlineUser.get().getId())){
          response.setEcode(EcodeConstant.NOT_ACCESS);
          response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
          return response;
        }else{
          Optional<UserSetting> onlineUserSetting = userSettingRepository.findByUserId(onlineUser.get().getId());

          data.put("disableAddFriend", onlineUserSetting.get().getDisableAddFriend());
          data.put("disableTag", onlineUserSetting.get().getDisableTag());

          response.setData(data);
          response.setEcode(EcodeConstant.SUCCESS);
          response.setMessage(EcodeConstant.SUCCESS_MSG);
          return response;
        }
    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }
}
