package com.social.demo.repository;

import com.social.demo.models.HiddenPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface HiddenPostsRepository  extends MongoRepository<HiddenPost, String> {
    void deleteByPostId(String id);

    Optional<HiddenPost> findByPostIdAndUserId(String postId, String userId);
}
