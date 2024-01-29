package com.social.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.Tag;

public interface TagRepository extends MongoRepository<Tag, String> {

    void deleteByReferenceId(String id);
}
