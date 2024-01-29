package com.social.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.Comments;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comments, String> {
    List<Comments> findByReferenceId(String referenceId);
    Page<Comments> getByReferenceIdOrderByCreateDate(String referenceId, Pageable pageable);

    Page<Comments> getByPostIdOrderByCreateDate(String postId, Pageable paging);

    Page<Comments> getByReferenceIdAndStatusOrderByCreateDate(String referenceId,String status, Pageable paging);

    Page<Comments> getByPostIdAndLevelOrderByCreateDate(String postId, int level, Pageable paging);

    Page<Comments> getByPostIdAndLevelAndStatusOrderByCreateDate(String postId, Integer level, String active, Pageable paging);

    Integer countByReferenceIdAndStatus(String referenceId, String status);

    void deleteById(String commentId);

    void deleteByPostId(String postId);

}

