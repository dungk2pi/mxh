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
import com.social.demo.models.Reacts;
import com.social.demo.models.Users;
import com.social.demo.payload.request.GetReactRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.ReactRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.GetReactService;

@Service
public class GetReactServiceImpl implements GetReactService {
  private static final Logger log = LogManager.getLogger(GetReactServiceImpl.class);

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private ReactRepository reactRepository;

  @Override
  public CommonResponse<Object> getReacts(GetReactRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();


    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);

      List<Reacts> _reacts = new ArrayList<Reacts>();

      Page<Reacts> pageReacts;

      Pageable paging = PageRequest.of(request.getPage(), 10);

      pageReacts = reactRepository.findByReferenceIdAndStatus(request.getReferenceId(), 1, paging);

      _reacts = pageReacts.getContent();
      log.info("list react size {}", _reacts.size());

      ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();

      for(Reacts item : _reacts){
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        data.put("userId", item.getUserId());
        Optional<Users> author = usersRepository.findById(item.getUserId());
        data.put("avatar", author.get().getAvatar());
        data.put("username", author.get().getUsername());
        data.put("type", item.getType());

        newArr.add(data);
      }
      response.setData(newArr);
      response.setMessage(EcodeConstant.SUCCESS_MSG);
      response.setEcode(EcodeConstant.SUCCESS);

      return response;
    } catch (Exception e) {
      log.error("Exception during get all react {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

}
