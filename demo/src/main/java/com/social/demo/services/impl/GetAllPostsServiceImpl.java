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
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.Comments;
import com.social.demo.models.Media;
import com.social.demo.models.Posts;
import com.social.demo.models.Reacts;
import com.social.demo.models.UserSetting;
import com.social.demo.models.Users;
import com.social.demo.payload.request.GetAllPostsRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.CommentRepository;
import com.social.demo.repository.MediaRepository;
import com.social.demo.repository.PostsRepository;
import com.social.demo.repository.ReactRepository;
import com.social.demo.repository.UserSettingRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.GetAllPostsService;

@Service
public class GetAllPostsServiceImpl implements GetAllPostsService {
  private static final Logger log = LogManager.getLogger(GetAllPostsServiceImpl.class);

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private PostsRepository postsRepository;

  @Autowired
  private MediaRepository mediaRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserSettingRepository userSettingRepository;

  @Autowired
  private ReactRepository reactRepository;


  @Override
  public CommonResponse<Object> getAllPosts(GetAllPostsRequest request,String username) {
    CommonResponse<Object> response = new CommonResponse<>();
    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      List<Posts> _posts = new ArrayList<Posts>();

      Page<Posts> pagePosts;

      Pageable paging = PageRequest.of(request.getPage(), 10);


      pagePosts = postsRepository.findByStatusAndReportStatusOrderByLastReactDateDesc("public", "active",paging);
      _posts = pagePosts.getContent();

      log.info("post size {}", _posts.size());

      ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

      for(Posts item : _posts){
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        List<Media> _medias = mediaRepository.findByReferenceIdOrderBySeqAsc(item.getId());
        Optional<Users> authorPost = usersRepository.findById(item.getUserId());


        Optional<UserSetting> authSettingPost = userSettingRepository.findByUserId(authorPost.get().getId());

        if(authSettingPost.isEmpty() || !authSettingPost.get().getListBlockAll().contains(onlineUser.get().getId())){
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

      }
      response.setData(newArr);

      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);
      return response;
    } catch (Exception e) {
      log.error("Exception during get all posts {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }


  @Override
  public CommonResponse<Object> getPostDetail(GetAllPostsRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();

    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      LinkedHashMap<String, Object> data = new LinkedHashMap<>();

      Optional<Posts> _postDetail = postsRepository.findByIdAndReportStatus(request.getPostId(), "active");
      Optional<Users> authorPost = usersRepository.findById(_postDetail.get().getUserId());

      Optional<UserSetting> authSettingPost = userSettingRepository.findByUserId(authorPost.get().getId());

      if(authSettingPost.isEmpty() || !authSettingPost.get().getListBlockAll().contains(onlineUser.get().getId())){
        if(_postDetail.isEmpty()){
          response.setEcode(EcodeConstant.NULL);
          response.setMessage(EcodeConstant.NULL_MSG);
          return response;
        }else{
          if(_postDetail.get().getStatus() == "private" && _postDetail.get().getId() != onlineUser.get().getId()){
            response.setEcode(EcodeConstant.NOT_ACCESS);
            response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
            return response;
          }else{
              data.put("postId", _postDetail.get().getId());

              LinkedHashMap<String, String> author = new LinkedHashMap<>();
              author.put("authorId", authorPost.get().getId());
              author.put("authorName", authorPost.get().getUsername());
              author.put("authorAvatar", authorPost.get().getAvatar());
              data.put("poster",author);

              data.put("content", _postDetail.get().getContent());
              data.put("status", _postDetail.get().getStatus());


              List<Media> _medias = mediaRepository.findByReferenceIdOrderBySeqAsc(_postDetail.get().getId());
              List<Comments> _comments = commentRepository.findAll();
              List<Reacts> _reacts = reactRepository.findAll();

              Optional<Reacts> _hasReacted = reactRepository.findByReferenceIdAndUserId(_postDetail.get().getId(),onlineUser.get().getId());

              data.put("commentCount", _comments.size());
              data.put("likeCounts", _reacts.size());

              if(_hasReacted.isPresent()){
                data.put("hasReacted", true);
              }else{
                data.put("hasReacted", false);
              }

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
              data.put("createDate", _postDetail.get().getCreateDate());
          }
        }
      }else{
        response.setEcode(EcodeConstant.NOT_ACCESS);
        response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
        return response;
      }



      response.setData(data);
      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);
      return response;
    } catch (Exception e) {
      log.error("Exception during get all posts {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }

  }

}
