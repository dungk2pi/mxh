package com.social.demo.services.impl;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.Appeal;
import com.social.demo.models.Posts;
import com.social.demo.models.Users;
import com.social.demo.payload.request.AppealRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.AppealRepository;
import com.social.demo.repository.PostsRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.AppealService;

@Service
public class AppealServiceImpl implements AppealService{
  private static final Logger log = LogManager.getLogger(AppealServiceImpl.class);

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private PostsRepository postsRepository;

  @Autowired
  private AppealRepository appealRepository;

  @Override
  public CommonResponse<Object> appealPost(AppealRequest request, String username) {
  CommonResponse<Object> response = new CommonResponse<>();

   try {
    Optional<Users> onlineUser = usersRepository.findByUsername(username);

    Optional<Posts> postReported = postsRepository.findByIdAndReportStatus(request.getPostId(), EcodeConstant.POST_STT_REPORTED);

    final String appealPostId = "appealPost_" + UUID.randomUUID().toString().replace("-", "");


    if(postReported.isEmpty()){
      response.setEcode(EcodeConstant.NULL);
      response.setMessage(EcodeConstant.NULL_MSG);
      return response;
    }else{
      Appeal appealPost = new Appeal();
      appealPost.setId(appealPostId);
      appealPost.setContent(request.getContent());
      appealPost.setUserId(onlineUser.get().getId());
      appealPost.setReferenceId(postReported.get().getId());
      appealPost.setStatus(1);

      Calendar c = Calendar.getInstance();
			c.add(Calendar.HOUR, 7);
		  Date now = c.getTime();
      appealPost.setCreateDate(now);
      appealPost.setEdiDate(now);

      appealRepository.save(appealPost);
    }

    response.setData(appealPostId);
    response.setEcode(EcodeConstant.SUCCESS);
    response.setMessage(EcodeConstant.SUCCESS_MSG);

    return response;
   } catch (Exception e) {
    log.error("Exception during appeal post {} |", e);
    response.setEcode(EcodeConstant.EXCEPTION);
    response.setMessage(EcodeConstant.EXCEPTION_MSG);
    return response;
   }
  }

}
