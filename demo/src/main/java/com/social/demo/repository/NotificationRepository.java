package com.social.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.Notifications;


public interface NotificationRepository extends MongoRepository<Notifications, String> {

}
