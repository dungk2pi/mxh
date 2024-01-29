package com.social.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.AddFriend;


public interface AddFriendRepository extends MongoRepository<AddFriend, String>{
  Optional<AddFriend> findByUserReqIdAndUserRevIdOrUserReqIdAndUserRevId(String userId1, String userId2, String userId2Reverse, String userId1Reverse);

  Optional<AddFriend> findByUserReqIdAndUserRevId(String userReqId, String userRevId);

  List<AddFriend> findByUserRevId(String userRevId);
}
