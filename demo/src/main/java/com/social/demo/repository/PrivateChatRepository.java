package com.social.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.PrivateChat;

public interface PrivateChatRepository extends MongoRepository<PrivateChat, String>{
  Page<PrivateChat> findByMergeIdOrderBySentDateDesc(String mergeId, Pageable pageable);
}
