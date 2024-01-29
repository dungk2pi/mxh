package com.social.demo.repository;

import java.util.Optional;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.UserSetting;


public interface UserSettingRepository extends MongoRepository<UserSetting, String> {
  Optional<UserSetting> findByUserId(String userId);
}
