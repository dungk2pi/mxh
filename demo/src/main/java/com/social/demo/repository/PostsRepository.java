package com.social.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.social.demo.models.Posts;


public interface PostsRepository extends MongoRepository<Posts, String> {
  Page<Posts> findByStatusAndReportStatusOrderByLastReactDateDesc(String status, String reportStatus, Pageable pageable);

  Page<Posts> findByUserIdAndReportStatusOrderByLastReactDateDesc(String userId, String reportStatus, Pageable pageable);

  Page<Posts> findByUserIdAndStatusAndReportStatusOrderByLastReactDateDesc(String userId,String status, String reportStatus, Pageable pageable);

  Page<Posts> findByReportStatusOrderByLastReactDateDesc(String reportStatus, Pageable pageable);

  Optional<Posts> findByIdAndReportStatus(String id, String reportStatus);

  Posts findPostsById(String id);
}
