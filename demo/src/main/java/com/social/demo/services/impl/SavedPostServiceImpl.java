package com.social.demo.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;


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
import com.social.demo.models.SavedPost;
import com.social.demo.models.UserSetting;
import com.social.demo.models.Users;
import com.social.demo.payload.request.SavedPostRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.CommentRepository;
import com.social.demo.repository.MediaRepository;
import com.social.demo.repository.PostsRepository;
import com.social.demo.repository.ReactRepository;
import com.social.demo.repository.SavedPostRepository;
import com.social.demo.repository.UserSettingRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.SavedPostService;

@Service
public class SavedPostServiceImpl implements SavedPostService {
  private static final Logger log = LogManager.getLogger(SavedPostServiceImpl.class);

  @Autowired
  private SavedPostRepository savedPostRepository;

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
  public CommonResponse<Object> saveOrUnsavePost(SavedPostRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      LinkedHashMap<String, Object> data = new LinkedHashMap<>();

      final String savedPostId = "savedpost_" + UUID.randomUUID().toString().replace("-", "");

      Optional<Posts> post = postsRepository.findByIdAndReportStatus(request.getPostId(), "active");

      Optional<Users> authorPost = usersRepository.findById(post.get().getUserId());

      Optional<UserSetting> authSettingPost = userSettingRepository.findByUserId(authorPost.get().getId());

      if(authSettingPost.isEmpty() || !authSettingPost.get().getListBlockAll().contains(onlineUser.get().getId())){
        if(post.isEmpty()){
          log.error("bài post không tồn tại");
          response.setEcode(EcodeConstant.NULL);
          response.setMessage(EcodeConstant.NULL_MSG);
          return response;
        }else{
          SavedPost savedPost = new SavedPost();
          if(request.getFlag().equals("save")){
            Optional<SavedPost> savedPostExist = savedPostRepository.findByPostIdAndUserId(request.getPostId(), onlineUser.get().getId());
            if(!savedPostExist.isEmpty()){
              log.error("bài post đã lưu không tồn tại trong th save");
              response.setEcode(EcodeConstant.NULL);
              response.setMessage(EcodeConstant.NULL_MSG);
              return response;
            }else{
              savedPost.setId(savedPostId);
              savedPost.setUserId(onlineUser.get().getId());
              savedPost.setPostId(post.get().getId());

              Calendar c = Calendar.getInstance();
              c.add(Calendar.HOUR, 7);
              Date now = c.getTime();

              savedPost.setSaveDate(now);
              savedPost.setStatus(request.getFlag());

              savedPostRepository.save(savedPost);
              log.info("Save new savedpost to DB success | PostId = {}", savedPostId);

              data.put("savedpostId", savedPostId);

              response.setData(data);
              response.setEcode(EcodeConstant.SUCCESS);
              response.setMessage(EcodeConstant.SUCCESS_MSG);

              return response;
            }
          }else{
            Optional<SavedPost> savedPostExist = savedPostRepository.findByPostIdAndUserId(request.getPostId(), onlineUser.get().getId());

            if(savedPostExist.isEmpty()){
              log.error("bài post đã lưu không tồn tại trong th unsave");
              response.setEcode(EcodeConstant.NULL);
              response.setMessage(EcodeConstant.NULL_MSG);
              return response;
            }else{
              savedPostRepository.delete(savedPostExist.get());
            }
            response.setEcode(EcodeConstant.SUCCESS);
            response.setMessage(EcodeConstant.SUCCESS_MSG);
            return response;
          }
        }
      }else{
        response.setEcode(EcodeConstant.NOT_ACCESS);
        response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
        return response;
      }

    } catch (Exception e) {
      log.error("Exception during save posts {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @Override
  public CommonResponse<Object> getSavePost(SavedPostRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      List<SavedPost> _savedposts = new ArrayList<SavedPost>();

      Page<SavedPost> pagesavedPosts;

      Pageable paging = PageRequest.of(request.getPage(), 10);

      pagesavedPosts = savedPostRepository.findByUserId("active", paging);
      _savedposts = pagesavedPosts.getContent();

      log.info("save post size {}", _savedposts.size());

      ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

      for(SavedPost item : _savedposts){
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        Optional<Posts> postInSave = postsRepository.findById(item.getPostId());

        List<Media> _medias = mediaRepository.findByReferenceIdOrderBySeqAsc(postInSave.get().getId());
        Optional<Users> authorPost = usersRepository.findById(item.getUserId());


        data.put("postId", postInSave.get().getId());
        data.put("content", postInSave.get().getContent());

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

        data.put("createDate",  postInSave.get().getCreateDate());

        LinkedHashMap<String, String> author = new LinkedHashMap<>();
        author.put("authorId", authorPost.get().getId());
        author.put("username", authorPost.get().getUsername());
        author.put("authorAvatar", authorPost.get().getAvatar());
        data.put("poster",author);

        List<Comments> _comments = commentRepository.findByReferenceId(postInSave.get().getId());
        List<Reacts> _reacts = reactRepository.findByReferenceId(postInSave.get().getId());

        Optional<Reacts> _hasReacted = reactRepository.findByReferenceIdAndUserId(postInSave.get().getId(),onlineUser.get().getId());

        data.put("commentCount", _comments.size());
        data.put("likeCounts", _reacts.size());

        if(_hasReacted.isPresent()){
          data.put("hasReacted", true);
        }else{
          data.put("hasReacted", false);
        }

        data.put("status", postInSave.get().getStatus());

        newArr.add(data);
      }

      return response;
    } catch (Exception e) {
      log.error("Exception during get my save posts {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

}
