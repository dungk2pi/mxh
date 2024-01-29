package com.social.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.Users;

public interface UserInfoRepository extends MongoRepository<Users, String> {

}
