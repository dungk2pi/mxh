package com.social.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.Appeal;

public interface AppealRepository extends MongoRepository<Appeal, String> {
}
