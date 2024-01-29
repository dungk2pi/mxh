package com.social.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.SavedPost;

public interface SavedPostRepository extends MongoRepository<SavedPost, String> {
  Optional<SavedPost> findByPostIdAndUserId(String postId, String userId);

  Page<SavedPost> findByUserId(String userId, Pageable pageable);

  void deleteByPostId(String id);
}
