package com.social.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.Reacts;

public interface ReactRepository extends MongoRepository<Reacts, String> {
  Optional<Reacts> findByReferenceIdAndUserId(String referenceId, String userId);

  List<Reacts> findByReferenceId(String referenceId);


  Page<Reacts> findByReferenceIdAndStatus(String referenceId, int status, Pageable pageable);

  void deleteByReferenceId(String id);
}
