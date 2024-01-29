package com.social.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.Media;
import java.util.List;


public interface MediaRepository extends MongoRepository<Media, String> {
  List<Media> findByReferenceIdOrderBySeqAsc(String referenceId);

  List<Media> findByReferenceIdAndTypeOrderBySeqAsc(String referenceId, String type);

  void deleteByReferenceId(String id);
}
